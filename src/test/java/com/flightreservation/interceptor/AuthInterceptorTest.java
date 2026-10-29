package com.flightreservation.interceptor;

import com.flightreservation.dto.SessionUser;
import com.flightreservation.util.AppConstants;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockHttpSession;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class AuthInterceptorTest {

    private final AuthInterceptor interceptor = new AuthInterceptor();

    @Test
    public void anonymousGet_isSentToLogin_andTargetIsRemembered() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/app/book/AI304");
        request.setContextPath("/app");
        request.setQueryString("x=1");
        MockHttpServletResponse response = new MockHttpServletResponse();

        boolean proceed = interceptor.preHandle(request, response, new Object());

        assertFalse(proceed);
        assertEquals("/app/login", response.getRedirectedUrl());
        assertEquals("/book/AI304?x=1",
                request.getSession().getAttribute(AppConstants.REDIRECT_AFTER_LOGIN));
    }

    @Test
    public void anonymousPost_isSentToLogin_butNothingIsRemembered() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/app/user/bookings/1/cancel");
        request.setContextPath("/app");
        MockHttpServletResponse response = new MockHttpServletResponse();

        boolean proceed = interceptor.preHandle(request, response, new Object());

        assertFalse(proceed);
        assertEquals("/app/login", response.getRedirectedUrl());
        assertNull(request.getSession(false));   // a POST cannot be replayed with a redirect
    }

    @Test
    public void loggedInUser_isLetThrough() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/app/user/dashboard");
        request.setContextPath("/app");
        MockHttpSession session = new MockHttpSession();
        session.setAttribute(AppConstants.SESSION_USER, new SessionUser(5L, "alice@email.com", "User"));
        request.setSession(session);
        MockHttpServletResponse response = new MockHttpServletResponse();

        assertTrue(interceptor.preHandle(request, response, new Object()));
        assertNull(response.getRedirectedUrl());
    }
}