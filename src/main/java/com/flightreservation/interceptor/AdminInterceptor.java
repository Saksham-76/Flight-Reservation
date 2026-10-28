package com.flightreservation.interceptor;

import com.flightreservation.dto.SessionUser;
import com.flightreservation.util.AppConstants;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Runs after AuthInterceptor, so a logged-in user is guaranteed here. */
public class AdminInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {

        SessionUser user = (SessionUser) request.getSession().getAttribute(AppConstants.SESSION_USER);
        if (user != null && AppConstants.ROLE_ADMIN.equals(user.getRole())) {
            return true;
        }
        response.sendError(HttpServletResponse.SC_FORBIDDEN, "Admin access only");
        return false;
    }
}