package com.example.userservice.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/*
 * AUTHOR: Paila Murali Madhav
 * CLASS: GlobalExceptionHandler
 * DESCRIPTION:
 *   Global exception handler for the User Service that maps application exceptions
 *   to structured HTTP error responses with timestamp, status, and message details.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /* ================================================================
     * METHOD: handleDuplicate
     * DESCRIPTION:
     *   Handles DuplicateException (duplicate email or phone) and returns a 409 Conflict response.
     * ================================================================ */
    @ExceptionHandler(DuplicateException.class)
    public ResponseEntity<Map<String, Object>> handleDuplicate(DuplicateException ex) {
        return buildResponse(HttpStatus.CONFLICT, ex.getMessage());
    }

    /* ================================================================
     * METHOD: handleNotFound
     * DESCRIPTION:
     *   Handles NotFoundException and returns a 404 Not Found response.
     * ================================================================ */
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleNotFound(NotFoundException ex) {
        return buildResponse(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    /* ================================================================
     * METHOD: handleInvalidCredentials
     * DESCRIPTION:
     *   Handles InvalidDataException (wrong password or invalid input) and returns a 401 Unauthorized response.
     * ================================================================ */
    @ExceptionHandler(InvalidDataException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidCredentials(InvalidDataException ex) {
        return buildResponse(HttpStatus.UNAUTHORIZED, ex.getMessage());
    }

    /* ================================================================
     * METHOD: buildResponse
     * DESCRIPTION:
     *   Builds a standardised error response body with timestamp, status, and message.
     * ================================================================ */
    private ResponseEntity<Map<String, Object>> buildResponse(HttpStatus status, String message) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now().toString());
        body.put("status",    status.value());
        body.put("error",     status.getReasonPhrase());
        body.put("message",   message);
        return new ResponseEntity<>(body, status);
    }
}
