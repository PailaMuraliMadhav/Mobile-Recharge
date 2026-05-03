package com.example.operatorservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/*
 * AUTHOR: Paila Murali Madhav
 * CLASS: GlobalExceptionHandler
 * DESCRIPTION:
 *   Global exception handler for the Operator Service that maps application exceptions
 *   to structured HTTP error responses with timestamp, status, and message details.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

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
     * METHOD: handleDuplicate
     * DESCRIPTION:
     *   Handles DuplicateException and returns a 409 Conflict response.
     * ================================================================ */
    @ExceptionHandler(DuplicateException.class)
    public ResponseEntity<Map<String, Object>> handleDuplicate(DuplicateException ex) {
        return buildResponse(HttpStatus.CONFLICT, ex.getMessage());
    }

    /* ================================================================
     * METHOD: handleValidation
     * DESCRIPTION:
     *   Handles bean validation failures and returns a 400 response with field-level error details.
     * ================================================================ */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> fieldErrors = new HashMap<>();
        ex.getBindingResult().getFieldErrors()
                .forEach(error -> fieldErrors.put(error.getField(), error.getDefaultMessage()));

        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now().toString());
        body.put("status",    400);
        body.put("error",     "Validation Failed");
        body.put("messages",  fieldErrors);

        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    /* ================================================================
     * METHOD: handleGeneral
     * DESCRIPTION:
     *   Catches all unhandled exceptions and returns a 500 Internal Server Error response.
     * ================================================================ */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGeneral(Exception ex) {
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR,
                "Something went wrong: " + ex.getMessage());
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
