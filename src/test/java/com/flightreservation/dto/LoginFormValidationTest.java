package com.flightreservation.dto;

import org.junit.BeforeClass;
import org.junit.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.Assert.assertTrue;

public class LoginFormValidationTest {

    private static Validator validator;

    @BeforeClass
    public static void createValidator() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    private LoginForm form(String email, String password) {
        LoginForm form = new LoginForm();
        form.setEmail(email);
        form.setPassword(password);
        return form;
    }

    private Set<String> messages(LoginForm form, String property) {
        return validator.validateProperty(form, property).stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.toSet());
    }

    @Test
    public void validForm_hasNoViolations() {
        assertTrue(validator.validate(form("alice@email.com", "anything")).isEmpty());
    }

    @Test
    public void bothFieldsAreRequired() {
        LoginForm empty = form(null, null);

        assertTrue(messages(empty, "email").contains("Email is required"));
        assertTrue(messages(empty, "password").contains("Password is required"));
    }

    @Test
    public void malformedEmailIsRejected() {
        assertTrue(messages(form("not-an-email", "x"), "email").contains("Enter a valid email address"));
    }

    @Test
    public void passwordFormatIsNotEnforcedOnLogin() {
        // Format rules belong on sign-up only; login just needs a value to compare
        assertTrue(messages(form("alice@email.com", "a"), "password").isEmpty());
    }
}