package com.flightreservation.mapper;

import com.flightreservation.dto.SessionUser;
import com.flightreservation.dto.SignupForm;
import com.flightreservation.model.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    /** SignupForm -> User entity. The caller passes the already-hashed password. */
    public User toEntity(SignupForm form, String encodedPassword) {
        if (form == null) {
            return null;
        }
        User user = new User();
        user.setEmail(form.getEmail().trim().toLowerCase());
        user.setPassword(encodedPassword);
        user.setRole(form.getRole());
        return user;
    }

    /** User entity -> SessionUser. The password hash is deliberately left out. */
    public SessionUser toSessionUser(User user) {
        if (user == null) {
            return null;
        }
        return new SessionUser(user.getUserId(), user.getEmail(), user.getRole());
    }
}