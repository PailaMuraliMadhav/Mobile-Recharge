package com.example.paymentservice.exception;

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
 *   Global exception handler for the Payment Service that maps application exceptions
 *   to structured HTTP error responses with timestamp, status, and message details.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final String KEY_TIMESTAMP = "timestamp";
    private static final String KEY_STATUS    = "status";
    private static final String KEY_ERROR     = "error";
    private static final String KEY_MESSAGE   = "message";

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
     * METHOD: handleValidation
     * DESCRIPTION:
     *   Handles bean validation failures and returns a 400 response with field-level error details.
     * ================================================================ */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> fieldErrors = new HashMap<>();
        ex.getBindingResult()
                .getFieldErrors()
                .forEach(error -> fieldErrors.put(error.getField(), error.getDefaultMessage()));
        Map<String, Object> body = new HashMap<>();
        body.put(KEY_TIMESTAMP, LocalDateTime.now());
        body.put(KEY_STATUS, 400);
        body.put(KEY_ERROR, "Validation Failed");
        body.put(KEY_MESSAGE, fieldErrors);
        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    /* ================================================================
     * METHOD: handleIllegalArgument
     * DESCRIPTION:
     *   Handles IllegalArgumentException (e.g. invalid payment mode) with a 400 response.
     * ================================================================ */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgument(IllegalArgumentException ex) {
        return buildResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    /* ================================================================
     * METHOD: handleRuntimeException
     * DESCRIPTION:
     *   Handles generic runtime exceptions with a 400 response.
     * ================================================================ */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, Object>> handleRuntimeException(RuntimeException ex) {
        return buildResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    /* ================================================================
     * METHOD: buildResponse
     * DESCRIPTION:
     *   Builds a standardised error response body with timestamp, status, and message.
     * ================================================================ */
    private ResponseEntity<Map<String, Object>> buildResponse(HttpStatus status, String message) {
        Map<String, Object> body = new HashMap<>();
        body.put(KEY_TIMESTAMP, LocalDateTime.now());
        body.put(KEY_STATUS, status.value());
        body.put(KEY_ERROR, status.getReasonPhrase());
        body.put(KEY_MESSAGE, message);
        return new ResponseEntity<>(body, status);
    }
}
