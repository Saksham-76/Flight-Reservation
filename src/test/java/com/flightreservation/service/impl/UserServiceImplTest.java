package com.flightreservation.service.impl;

import com.flightreservation.dto.LoginForm;
import com.flightreservation.dto.SessionUser;
import com.flightreservation.dto.SignupForm;
import com.flightreservation.exception.DuplicateResourceException;
import com.flightreservation.exception.InvalidCredentialsException;
import com.flightreservation.mapper.UserMapper;
import com.flightreservation.model.User;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class UserServiceImplTest {

    @Mock private NamedParameterJdbcTemplate jdbc;
    @Mock private PasswordEncoder passwordEncoder;
    @Captor private ArgumentCaptor<Map<String, Object>> paramsCaptor;

    private UserServiceImpl userService;

    @Before
    public void setUp() {
        userService = new UserServiceImpl(jdbc, passwordEncoder, new UserMapper());
    }

    // ---------------- helpers ----------------

    private SignupForm signupForm(String email, String password, String role) {
        SignupForm form = new SignupForm();
        form.setEmail(email);
        form.setPassword(password);
        form.setRole(role);
        return form;
    }

    private LoginForm loginForm(String email, String password) {
        LoginForm form = new LoginForm();
        form.setEmail(email);
        form.setPassword(password);
        return form;
    }

    private User storedUser() {
        User user = new User("alice@email.com", "HASH", "User");
        user.setUserId(5L);
        return user;
    }

    /** Makes the mocked INSERT behave like MySQL: it fills the KeyHolder with a generated id. */
    private void stubInsertGeneratingId(long id) {
        doAnswer(invocation -> {
            KeyHolder keyHolder = invocation.getArgument(2);
            Map<String, Object> key = new HashMap<>();
            key.put("GENERATED_KEY", id);
            keyHolder.getKeyList().add(key);
            return 1;
        }).when(jdbc).update(anyString(), any(SqlParameterSource.class), any(KeyHolder.class));
    }

    private void stubFindByEmail(List<User> result) {
        when(jdbc.query(anyString(), anyMap(), ArgumentMatchers.<RowMapper<User>>any()))
                .thenReturn(result);
    }

    // ---------------- register ----------------

    @Test
    public void register_savesUserWithHashedPassword_andReturnsGeneratedId() {
        when(jdbc.queryForObject(anyString(), anyMap(), eq(Integer.class))).thenReturn(0);
        when(passwordEncoder.encode("Secret123")).thenReturn("HASH");
        stubInsertGeneratingId(7L);

        User saved = userService.register(signupForm("  Alice@Email.com ", "Secret123", "User"));

        assertEquals(Long.valueOf(7L), saved.getUserId());
        assertEquals("alice@email.com", saved.getEmail());
        assertEquals("HASH", saved.getPassword());

        ArgumentCaptor<SqlParameterSource> inserted = ArgumentCaptor.forClass(SqlParameterSource.class);
        verify(jdbc).update(anyString(), inserted.capture(), any(KeyHolder.class));
        assertEquals("alice@email.com", inserted.getValue().getValue("email"));
        assertEquals("HASH", inserted.getValue().getValue("password"));
        assertNotEquals("Secret123", inserted.getValue().getValue("password"));
        assertEquals("User", inserted.getValue().getValue("role"));
    }

    @Test
    public void register_rejectsExistingEmail_andNeverInserts() {
        when(jdbc.queryForObject(anyString(), anyMap(), eq(Integer.class))).thenReturn(1);

        DuplicateResourceException ex = assertThrows(DuplicateResourceException.class,
                () -> userService.register(signupForm("alice@email.com", "Secret123", "User")));

        assertEquals("An account with this email already exists", ex.getMessage());
        verify(jdbc, never()).update(anyString(), any(SqlParameterSource.class), any(KeyHolder.class));
    }

    @Test
    public void register_translatesUniqueKeyViolationFromRaceCondition() {
        // existsByEmail says "free", but another request inserted the same email a moment earlier
        when(jdbc.queryForObject(anyString(), anyMap(), eq(Integer.class))).thenReturn(0);
        doThrow(new DuplicateKeyException("Duplicate entry"))
                .when(jdbc).update(anyString(), any(SqlParameterSource.class), any(KeyHolder.class));

        assertThrows(DuplicateResourceException.class,
                () -> userService.register(signupForm("alice@email.com", "Secret123", "User")));
    }

    // ---------------- login ----------------

    @Test
    public void login_withCorrectPassword_returnsSessionUser() {
        stubFindByEmail(List.of(storedUser()));
        when(passwordEncoder.matches("Secret123", "HASH")).thenReturn(true);

        SessionUser result = userService.login(loginForm("alice@email.com", "Secret123"));

        assertEquals(Long.valueOf(5L), result.getUserId());
        assertEquals("alice@email.com", result.getEmail());
        assertEquals("User", result.getRole());
    }

    @Test
    public void login_normalizesEmailBeforeLookup() {
        stubFindByEmail(List.of(storedUser()));
        when(passwordEncoder.matches("Secret123", "HASH")).thenReturn(true);

        userService.login(loginForm("  ALICE@Email.com ", "Secret123"));

        verify(jdbc).query(anyString(), paramsCaptor.capture(),
                ArgumentMatchers.<RowMapper<User>>any());
        assertEquals("alice@email.com", paramsCaptor.getValue().get("email"));
    }

    @Test
    public void login_withWrongPassword_throwsInvalidCredentials() {
        stubFindByEmail(List.of(storedUser()));
        when(passwordEncoder.matches("WrongPass1", "HASH")).thenReturn(false);

        assertThrows(InvalidCredentialsException.class,
                () -> userService.login(loginForm("alice@email.com", "WrongPass1")));
    }

    @Test
    public void login_withUnknownEmail_throwsInvalidCredentials_withoutCheckingPassword() {
        stubFindByEmail(Collections.<User>emptyList());

        assertThrows(InvalidCredentialsException.class,
                () -> userService.login(loginForm("nobody@email.com", "Secret123")));

        verifyNoInteractions(passwordEncoder);
    }

    @Test
    public void login_failureMessageIsTheSameForUnknownEmailAndWrongPassword() {
        // Security: the message must not reveal whether an account exists
        when(jdbc.query(anyString(), anyMap(), ArgumentMatchers.<RowMapper<User>>any()))
                .thenReturn(Collections.<User>emptyList(), List.of(storedUser()));
        when(passwordEncoder.matches("WrongPass1", "HASH")).thenReturn(false);

        InvalidCredentialsException unknownEmail = assertThrows(InvalidCredentialsException.class,
                () -> userService.login(loginForm("nobody@email.com", "WrongPass1")));
        InvalidCredentialsException wrongPassword = assertThrows(InvalidCredentialsException.class,
                () -> userService.login(loginForm("alice@email.com", "WrongPass1")));

        assertEquals(unknownEmail.getMessage(), wrongPassword.getMessage());
        assertEquals("Invalid email or password", wrongPassword.getMessage());
    }
}