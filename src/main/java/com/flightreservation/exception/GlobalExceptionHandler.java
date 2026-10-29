package com.flightreservation.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.TypeMismatchException;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.NoHandlerFoundException;

import javax.servlet.http.HttpServletRequest;
import java.util.UUID;

/**
 * One place that turns exceptions into an error page with the right HTTP status.
 * Users never see stack traces; unexpected failures get an error id that is also in the log.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // ---------- 404 ----------

    @ExceptionHandler(ResourceNotFoundException.class)
    public ModelAndView handleNotFound(ResourceNotFoundException ex) {
        return errorView(HttpStatus.NOT_FOUND, "Not Found", ex.getMessage(), null);
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public ModelAndView handleNoHandler(NoHandlerFoundException ex) {
        return errorView(HttpStatus.NOT_FOUND, "Page Not Found",
                "The page you are looking for does not exist.", null);
    }

    // ---------- 403 ----------

    @ExceptionHandler(ForbiddenException.class)
    public ModelAndView handleForbidden(ForbiddenException ex) {
        return errorView(HttpStatus.FORBIDDEN, "Access Denied", ex.getMessage(), null);
    }

    // ---------- 400 / 405 ----------

    @ExceptionHandler(TypeMismatchException.class)
    public ModelAndView handleBadParameter(TypeMismatchException ex) {
        return errorView(HttpStatus.BAD_REQUEST, "Bad Request",
                "The request contains an invalid value.", null);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ModelAndView handleMethodNotAllowed(HttpRequestMethodNotSupportedException ex) {
        return errorView(HttpStatus.METHOD_NOT_ALLOWED, "Method Not Allowed",
                "This action cannot be performed that way.", null);
    }

    // ---------- safety net for business exceptions normally caught in controllers ----------

    @ExceptionHandler({DuplicateResourceException.class,
                       SeatsUnavailableException.class,
                       BookingNotCancellableException.class})
    public ModelAndView handleConflict(RuntimeException ex) {
        return errorView(HttpStatus.CONFLICT, "Request Could Not Be Completed", ex.getMessage(), null);
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ModelAndView handleInvalidCredentials(InvalidCredentialsException ex) {
        return errorView(HttpStatus.UNAUTHORIZED, "Authentication Failed", ex.getMessage(), null);
    }

    // ---------- 500 ----------

    @ExceptionHandler(DataAccessException.class)
    public ModelAndView handleDatabase(DataAccessException ex, HttpServletRequest request) {
        String errorId = newErrorId();
        log.error("Database error [{}] on {} {}", errorId, request.getMethod(), request.getRequestURI(), ex);
        return errorView(HttpStatus.INTERNAL_SERVER_ERROR, "Service Unavailable",
                "We could not complete your request because of a database problem. Please try again.",
                errorId);
    }

    @ExceptionHandler(Exception.class)
    public ModelAndView handleUnexpected(Exception ex, HttpServletRequest request) {
        String errorId = newErrorId();
        log.error("Unexpected error [{}] on {} {}", errorId, request.getMethod(), request.getRequestURI(), ex);
        return errorView(HttpStatus.INTERNAL_SERVER_ERROR, "Something Went Wrong",
                "An unexpected error occurred. Please try again later.", errorId);
    }

    // ---------- helpers ----------

    private ModelAndView errorView(HttpStatus status, String title, String message, String errorId) {
        ModelAndView mav = new ModelAndView("error");
        mav.setStatus(status);
        mav.addObject("errorStatus", status.value());
        mav.addObject("errorTitle", title);
        mav.addObject("errorMessage", message);
        mav.addObject("errorId", errorId);
        return mav;
    }

    private String newErrorId() {
        return UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}