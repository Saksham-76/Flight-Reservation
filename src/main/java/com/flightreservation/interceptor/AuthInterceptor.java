package com.flightreservation.interceptor;

import com.flightreservation.util.AppConstants;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/** Blocks protected URLs for anonymous users and remembers where they wanted to go. */
public class AuthInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {

        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute(AppConstants.SESSION_USER) != null) {
            return true;
        }

        // Only GET pages can be revisited after login (a POST cannot be replayed with a redirect)
        if ("GET".equalsIgnoreCase(request.getMethod())) {
            String target = request.getRequestURI().substring(request.getContextPath().length());
            if (request.getQueryString() != null) {
                target += "?" + request.getQueryString();
            }
            request.getSession(true).setAttribute(AppConstants.REDIRECT_AFTER_LOGIN, target);
        }

        response.sendRedirect(request.getContextPath() + "/login");
        return false;
    }
}