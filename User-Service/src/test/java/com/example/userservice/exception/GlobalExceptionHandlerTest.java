package com.example.userservice.exception;

import com.example.userservice.exceptions.DuplicateException;
import com.example.userservice.exceptions.GlobalExceptionHandler;
import com.example.userservice.exceptions.InvalidDataException;
import com.example.userservice.exceptions.NotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    // Duplicate Exception -> 409
    @Test
    void handleDuplicate_shouldReturnConflict() {

        DuplicateException ex = new DuplicateException("Email already exists");

        ResponseEntity<Map<String, Object>> response =
                handler.handleDuplicate(ex);

        assertEquals(409, response.getStatusCode().value());
        assertEquals("Email already exists", response.getBody().get("message"));
        assertEquals("Conflict", response.getBody().get("error"));
    }

    // Not Found Exception -> 404
    @Test
    void handleNotFound_shouldReturnNotFound() {

        NotFoundException ex = new NotFoundException("User not found");

        ResponseEntity<Map<String, Object>> response =
                handler.handleNotFound(ex);

        assertEquals(404, response.getStatusCode().value());
        assertEquals("User not found", response.getBody().get("message"));
        assertEquals("Not Found", response.getBody().get("error"));
    }

    // Invalid Data Exception -> 401
    @Test
    void handleInvalidData_shouldReturnUnauthorized() {

        InvalidDataException ex = new InvalidDataException("Invalid password");

        ResponseEntity<Map<String, Object>> response =
                handler.handleInvalidCredentials(ex);

        assertEquals(401, response.getStatusCode().value());
        assertEquals("Invalid password", response.getBody().get("message"));
        assertEquals("Unauthorized", response.getBody().get("error"));
    }

    // Extra check for timestamp + status
    @Test
    void responseShouldContainAllFields() {

        DuplicateException ex = new DuplicateException("Duplicate data");

        ResponseEntity<Map<String, Object>> response =
                handler.handleDuplicate(ex);

        Map<String, Object> body = response.getBody();

        assertNotNull(body.get("timestamp"));
        assertEquals(409, body.get("status"));
        assertEquals("Conflict", body.get("error"));
        assertEquals("Duplicate data", body.get("message"));
    }
}