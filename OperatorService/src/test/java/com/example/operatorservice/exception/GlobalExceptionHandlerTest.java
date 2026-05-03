package com.example.operatorservice.exception;

import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/*
 * AUTHOR: Paila Murali Madhav
 * CLASS: GlobalExceptionHandlerTest
 * DESCRIPTION:
 *   Unit tests for OperatorService GlobalExceptionHandler.
 */
class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void handleNotFound_returns404() {
        ResponseEntity<Map<String, Object>> resp =
                handler.handleNotFound(new NotFoundException("Operator not found"));
        assertEquals(404, resp.getStatusCode().value());
        assertEquals("Operator not found", resp.getBody().get("message"));
        assertEquals("Not Found", resp.getBody().get("error"));
        assertNotNull(resp.getBody().get("timestamp"));
    }

    @Test
    void handleDuplicate_returns409() {
        ResponseEntity<Map<String, Object>> resp =
                handler.handleDuplicate(new DuplicateException("Operator already exists"));
        assertEquals(409, resp.getStatusCode().value());
        assertEquals("Operator already exists", resp.getBody().get("message"));
        assertEquals("Conflict", resp.getBody().get("error"));
    }

    @Test
    void handleGeneral_returns500() {
        ResponseEntity<Map<String, Object>> resp =
                handler.handleGeneral(new Exception("unexpected error"));
        assertEquals(500, resp.getStatusCode().value());
        assertTrue(resp.getBody().get("message").toString().contains("unexpected error"));
        assertEquals("Internal Server Error", resp.getBody().get("error"));
    }

    @Test
    void handleNotFound_bodyContainsStatus() {
        ResponseEntity<Map<String, Object>> resp =
                handler.handleNotFound(new NotFoundException("Plan not found"));
        assertEquals(404, resp.getBody().get("status"));
    }

    @Test
    void handleDuplicate_bodyContainsStatus() {
        ResponseEntity<Map<String, Object>> resp =
                handler.handleDuplicate(new DuplicateException("Duplicate plan"));
        assertEquals(409, resp.getBody().get("status"));
    }
}
