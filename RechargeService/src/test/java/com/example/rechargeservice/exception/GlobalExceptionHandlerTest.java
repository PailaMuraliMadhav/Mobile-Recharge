package com.example.rechargeservice.exception;

import com.example.rechargeservice.exceptions.BadRequestException;
import com.example.rechargeservice.exceptions.GlobalExceptionHandler;
import com.example.rechargeservice.exceptions.NotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/*
 * AUTHOR: Paila Murali Madhav
 * CLASS: GlobalExceptionHandlerTest
 * DESCRIPTION:
 *   Unit tests for RechargeService GlobalExceptionHandler.
 */
class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void handleNotFound_returns404() {
        ResponseEntity<Map<String, Object>> resp =
                handler.handleNotFound(new NotFoundException("Recharge not found"));
        assertEquals(404, resp.getStatusCode().value());
        assertEquals("Recharge not found", resp.getBody().get("message"));
        assertEquals("Not Found", resp.getBody().get("error"));
        assertNotNull(resp.getBody().get("timestamp"));
    }

    @Test
    void handleBadRequest_returns400() {
        ResponseEntity<Map<String, Object>> resp =
                handler.handleBadRequest(new BadRequestException("Operator inactive"));
        assertEquals(400, resp.getStatusCode().value());
        assertEquals("Operator inactive", resp.getBody().get("message"));
        assertEquals("Bad Request", resp.getBody().get("error"));
    }

    @Test
    void handleGeneral_returns500() {
        ResponseEntity<Map<String, Object>> resp =
                handler.handleGeneral(new Exception("unexpected"));
        assertEquals(500, resp.getStatusCode().value());
        assertTrue(resp.getBody().get("message").toString().contains("unexpected"));
    }

    @Test
    void handleNotFound_bodyContainsStatus() {
        ResponseEntity<Map<String, Object>> resp =
                handler.handleNotFound(new NotFoundException("not found"));
        assertEquals(404, resp.getBody().get("status"));
    }

    @Test
    void handleBadRequest_bodyContainsStatus() {
        ResponseEntity<Map<String, Object>> resp =
                handler.handleBadRequest(new BadRequestException("bad"));
        assertEquals(400, resp.getBody().get("status"));
    }
}
