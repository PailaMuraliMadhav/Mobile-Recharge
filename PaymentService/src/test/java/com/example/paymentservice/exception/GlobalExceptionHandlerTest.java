package com.example.paymentservice.exception;

import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/*
 * AUTHOR: Paila Murali Madhav
 * CLASS: GlobalExceptionHandlerTest
 * DESCRIPTION:
 *   Unit tests for PaymentService GlobalExceptionHandler covering all exception mappings.
 */
class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void handleNotFound_returns404() {
        ResponseEntity<?> resp = handler.handleNotFound(new NotFoundException("not found"));
        assertEquals(404, resp.getStatusCode().value());
        Map<?, ?> body = (Map<?, ?>) resp.getBody();
        assertEquals("not found", body.get("message"));
        assertEquals("Not Found", body.get("error"));
    }

    @Test
    void handleIllegalArgument_returns400() {
        ResponseEntity<?> resp = handler.handleIllegalArgument(new IllegalArgumentException("bad mode"));
        assertEquals(400, resp.getStatusCode().value());
        Map<?, ?> body = (Map<?, ?>) resp.getBody();
        assertEquals("bad mode", body.get("message"));
        assertEquals("Bad Request", body.get("error"));
    }

    @Test
    void handleRuntimeException_returns400() {
        ResponseEntity<?> resp = handler.handleRuntimeException(new RuntimeException("generic error"));
        assertEquals(400, resp.getStatusCode().value());
        Map<?, ?> body = (Map<?, ?>) resp.getBody();
        assertEquals("generic error", body.get("message"));
    }

    @Test
    void handleNotFound_bodyContainsTimestampAndStatus() {
        ResponseEntity<?> resp = handler.handleNotFound(new NotFoundException("tx not found"));
        Map<?, ?> body = (Map<?, ?>) resp.getBody();
        assertNotNull(body.get("timestamp"));
        assertEquals(404, body.get("status"));
    }
}
