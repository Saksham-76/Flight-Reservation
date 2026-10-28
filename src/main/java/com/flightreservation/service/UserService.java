package com.flightreservation.service;

import com.flightreservation.dto.LoginForm;
import com.flightreservation.dto.SessionUser;
import com.flightreservation.dto.SignupForm;
import com.flightreservation.model.User;

import java.util.Optional;

public interface UserService {

    /**
     * Registers a new user (password is hashed with BCrypt).
     *
     * @throws com.flightreservation.exception.DuplicateResourceException if the email already exists
     */
    User register(SignupForm form);

    /**
     * Verifies the credentials and returns the user details to keep in the session.
     *
     * @throws com.flightreservation.exception.InvalidCredentialsException on any mismatch
     */
    SessionUser login(LoginForm form);

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);
}