package com.flightreservation.controller;

import com.flightreservation.dto.LoginForm;
import com.flightreservation.dto.SessionUser;
import com.flightreservation.dto.SignupForm;
import com.flightreservation.exception.DuplicateResourceException;
import com.flightreservation.exception.InvalidCredentialsException;
import com.flightreservation.service.UserService;
import com.flightreservation.util.AppConstants;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@RunWith(MockitoJUnitRunner.class)
public class AuthControllerTest {

    @Mock private UserService userService;
    @InjectMocks private AuthController authController;

    private MockMvc mockMvc;

    @Before
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(authController)
                .setViewResolvers(new InternalResourceViewResolver("/WEB-INF/views/", ".jsp"))
                .build();
    }

    private MockHttpSession sessionWithLoggedInUser() {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute(AppConstants.SESSION_USER, new SessionUser(5L, "alice@email.com", "User"));
        return session;
    }

    // =============== Sign up ===============

    @Test
    public void getSignup_showsEmptyForm() throws Exception {
        mockMvc.perform(get("/signup"))
                .andExpect(status().isOk())
                .andExpect(view().name("signup"))
                .andExpect(model().attributeExists("signupForm"));
    }

    @Test
    public void getSignup_whenAlreadyLoggedIn_redirectsHome() throws Exception {
        mockMvc.perform(get("/signup").session(sessionWithLoggedInUser()))
                .andExpect(redirectedUrl("/"));
    }

    @Test
    public void postSignup_withValidData_registersAndRedirectsToLogin() throws Exception {
        mockMvc.perform(post("/signup")
                        .param("email", "alice@email.com")
                        .param("password", "Secret123")
                        .param("role", "User"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"))
                .andExpect(flash().attribute("successMessage",
                        "Account created successfully. Please log in."));

        verify(userService).register(any(SignupForm.class));
    }

    @Test
    public void postSignup_withEmptyForm_showsErrorsOnAllFields_andDoesNotCallService() throws Exception {
        mockMvc.perform(post("/signup"))
                .andExpect(status().isOk())
                .andExpect(view().name("signup"))
                .andExpect(model().attributeHasFieldErrors("signupForm", "email", "password", "role"));

        verify(userService, never()).register(any(SignupForm.class));
    }

    @Test
    public void postSignup_withInvalidEmail_showsEmailError() throws Exception {
        mockMvc.perform(post("/signup")
                        .param("email", "alice@")
                        .param("password", "Secret123")
                        .param("role", "User"))
                .andExpect(view().name("signup"))
                .andExpect(model().attributeHasFieldErrorCode("signupForm", "email", "Email"));

        verify(userService, never()).register(any(SignupForm.class));
    }

    @Test
    public void postSignup_withWeakPassword_showsPasswordError() throws Exception {
        mockMvc.perform(post("/signup")
                        .param("email", "alice@email.com")
                        .param("password", "abc")
                        .param("role", "User"))
                .andExpect(view().name("signup"))
                .andExpect(model().attributeHasFieldErrorCode("signupForm", "password", "Pattern"));

        verify(userService, never()).register(any(SignupForm.class));
    }

    @Test
    public void postSignup_withInvalidRole_showsRoleError() throws Exception {
        mockMvc.perform(post("/signup")
                        .param("email", "alice@email.com")
                        .param("password", "Secret123")
                        .param("role", "Manager"))
                .andExpect(view().name("signup"))
                .andExpect(model().attributeHasFieldErrorCode("signupForm", "role", "Pattern"));
    }

    @Test
    public void postSignup_withExistingEmail_showsDuplicateErrorOnEmailField() throws Exception {
        when(userService.register(any(SignupForm.class)))
                .thenThrow(new DuplicateResourceException("An account with this email already exists"));

        mockMvc.perform(post("/signup")
                        .param("email", "alice@email.com")
                        .param("password", "Secret123")
                        .param("role", "User"))
                .andExpect(status().isOk())
                .andExpect(view().name("signup"))
                .andExpect(model().attributeHasFieldErrorCode("signupForm", "email", "duplicate"));
    }

    // =============== Login ===============

    @Test
    public void getLogin_showsEmptyForm() throws Exception {
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("login"))
                .andExpect(model().attributeExists("loginForm"));
    }

    @Test
    public void getLogin_whenAlreadyLoggedIn_redirectsHome() throws Exception {
        mockMvc.perform(get("/login").session(sessionWithLoggedInUser()))
                .andExpect(redirectedUrl("/"));
    }

    @Test
    public void postLogin_withEmptyForm_showsErrors_andDoesNotCallService() throws Exception {
        mockMvc.perform(post("/login"))
                .andExpect(view().name("login"))
                .andExpect(model().attributeHasFieldErrors("loginForm", "email", "password"));

        verify(userService, never()).login(any(LoginForm.class));
    }

    @Test
    public void postLogin_withWrongCredentials_showsGenericError_andKeepsUserLoggedOut() throws Exception {
        when(userService.login(any(LoginForm.class))).thenThrow(new InvalidCredentialsException());

        mockMvc.perform(post("/login")
                        .param("email", "alice@email.com")
                        .param("password", "WrongPass1"))
                .andExpect(status().isOk())
                .andExpect(view().name("login"))
                .andExpect(model().attribute("loginError", "Invalid email or password"))
                .andExpect(request().sessionAttribute(AppConstants.SESSION_USER, nullValue()));
    }

    @Test
    public void postLogin_asUser_storesSessionUser_andGoesToUserDashboard() throws Exception {
        SessionUser user = new SessionUser(5L, "alice@email.com", "User");
        when(userService.login(any(LoginForm.class))).thenReturn(user);

        mockMvc.perform(post("/login")
                        .param("email", "alice@email.com")
                        .param("password", "Secret123"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/user/dashboard"))
                .andExpect(request().sessionAttribute(AppConstants.SESSION_USER, user));
    }

    @Test
    public void postLogin_asAdmin_goesToAdminDashboard() throws Exception {
        when(userService.login(any(LoginForm.class)))
                .thenReturn(new SessionUser(1L, "admin@email.com", "Admin"));

        mockMvc.perform(post("/login")
                        .param("email", "admin@email.com")
                        .param("password", "Secret123"))
                .andExpect(redirectedUrl("/admin/dashboard"));
    }

    @Test
    public void postLogin_returnsToTheProtectedPageTheUserWasSentAwayFrom() throws Exception {
        when(userService.login(any(LoginForm.class)))
                .thenReturn(new SessionUser(5L, "alice@email.com", "User"));
        MockHttpSession session = new MockHttpSession();
        session.setAttribute(AppConstants.REDIRECT_AFTER_LOGIN, "/book/AI304");

        mockMvc.perform(post("/login").session(session)
                        .param("email", "alice@email.com")
                        .param("password", "Secret123"))
                .andExpect(redirectedUrl("/book/AI304"))
                .andExpect(request().sessionAttribute(AppConstants.REDIRECT_AFTER_LOGIN, nullValue()));
    }

    // =============== Logout ===============

    @Test
    public void logout_invalidatesSession_andRedirectsHome() throws Exception {
        MockHttpSession session = sessionWithLoggedInUser();

        mockMvc.perform(get("/logout").session(session))
                .andExpect(redirectedUrl("/"));

        assertTrue("session should be invalidated", session.isInvalid());
    }
}