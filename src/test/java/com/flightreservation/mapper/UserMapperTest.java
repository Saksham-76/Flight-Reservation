package com.flightreservation.mapper;

import com.flightreservation.dto.SessionUser;
import com.flightreservation.dto.SignupForm;
import com.flightreservation.model.User;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class UserMapperTest {

    private final UserMapper mapper = new UserMapper();

    @Test
    public void toEntity_trimsAndLowercasesEmail_andUsesGivenHash() {
        SignupForm form = new SignupForm();
        form.setEmail("  Alice@Email.COM ");
        form.setPassword("Secret123");
        form.setRole("Admin");

        User user = mapper.toEntity(form, "HASH");

        assertEquals("alice@email.com", user.getEmail());
        assertEquals("HASH", user.getPassword());      // the hash, never the raw password
        assertEquals("Admin", user.getRole());
        assertNull(user.getUserId());
    }

    @Test
    public void toEntity_returnsNullForNullForm() {
        assertNull(mapper.toEntity(null, "HASH"));
    }

    @Test
    public void toSessionUser_copiesIdEmailAndRole() {
        User user = new User("alice@email.com", "HASH", "User");
        user.setUserId(5L);

        SessionUser sessionUser = mapper.toSessionUser(user);

        assertEquals(Long.valueOf(5L), sessionUser.getUserId());
        assertEquals("alice@email.com", sessionUser.getEmail());
        assertEquals("User", sessionUser.getRole());
    }

    @Test
    public void sessionUser_hasNoPasswordField() {
        // Regression guard: the object stored in the HTTP session must never carry the hash
        boolean hasPassword = Arrays.stream(SessionUser.class.getDeclaredFields())
                .anyMatch(f -> f.getName().toLowerCase().contains("password"));
        assertTrue("SessionUser must not hold a password", !hasPassword);
    }
}