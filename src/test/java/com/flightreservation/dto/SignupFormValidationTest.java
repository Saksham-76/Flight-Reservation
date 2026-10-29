package com.flightreservation.dto;

import org.junit.BeforeClass;
import org.junit.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class SignupFormValidationTest {

    private static Validator validator;

    @BeforeClass
    public static void createValidator() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    private SignupForm form(String email, String password, String role) {
        SignupForm form = new SignupForm();
        form.setEmail(email);
        form.setPassword(password);
        form.setRole(role);
        return form;
    }

    private Set<String> messages(SignupForm form, String property) {
        return validator.validateProperty(form, property).stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.toSet());
    }

    @Test
    public void validForm_hasNoViolations() {
        assertTrue(validator.validate(form("alice@email.com", "Secret123", "User")).isEmpty());
        assertTrue(validator.validate(form("admin@email.com", "abcd1234", "Admin")).isEmpty());
    }

    @Test
    public void allFieldsAreRequired() {
        SignupForm empty = form(null, null, null);

        assertTrue(messages(empty, "email").contains("Email is required"));
        assertTrue(messages(empty, "password").contains("Password is required"));
        assertTrue(messages(empty, "role").contains("Please select a role"));
    }

    @Test
    public void blankValuesAreRejected() {
        SignupForm blank = form("   ", "   ", "");

        assertFalse(messages(blank, "email").isEmpty());
        assertFalse(messages(blank, "password").isEmpty());
        assertFalse(messages(blank, "role").isEmpty());
    }

    @Test
    public void malformedEmailIsRejected() {
        for (String bad : new String[]{"alice", "alice@", "@email.com", "alice email@x.com"}) {
            assertTrue("should reject: " + bad,
                    messages(form(bad, "Secret123", "User"), "email").contains("Enter a valid email address"));
        }
    }

    @Test
    public void weakPasswordsAreRejected() {
        String rule = "Password must be at least 8 characters with a letter and a number";
        for (String weak : new String[]{"Ab1", "abcdefgh", "12345678", "short1"}) {
            assertTrue("should reject: " + weak,
                    messages(form("alice@email.com", weak, "User"), "password").contains(rule));
        }
    }

    @Test
    public void strongPasswordsAreAccepted() {
        for (String strong : new String[]{"Secret123", "abcd1234", "1234abcd", "P@ssw0rd!"}) {
            assertTrue("should accept: " + strong,
                    messages(form("alice@email.com", strong, "User"), "password").isEmpty());
        }
    }

    @Test
    public void roleMustBeUserOrAdmin() {
        assertTrue(messages(form("a@b.com", "Secret123", "User"), "role").isEmpty());
        assertTrue(messages(form("a@b.com", "Secret123", "Admin"), "role").isEmpty());
        assertTrue(messages(form("a@b.com", "Secret123", "Manager"), "role")
                .contains("Role must be User or Admin"));
        assertTrue(messages(form("a@b.com", "Secret123", "admin"), "role")
                .contains("Role must be User or Admin"));   // case-sensitive on purpose
    }
}