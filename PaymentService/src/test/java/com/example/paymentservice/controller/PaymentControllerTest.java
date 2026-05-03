package com.example.paymentservice.controller;

import com.example.paymentservice.dto.PaymentRequestDto;
import com.example.paymentservice.dto.PaymentResponseDto;
import com.example.paymentservice.enums.PaymentMode;
import com.example.paymentservice.enums.PaymentStatus;
import com.example.paymentservice.service.PaymentService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/*
 * AUTHOR: Paila Murali Madhav
 * CLASS: PaymentControllerTest
 * DESCRIPTION:
 *   Unit tests for PaymentController covering standard payment endpoints.
 */
@WebMvcTest(PaymentController.class)
@AutoConfigureMockMvc(addFilters = false)
class PaymentControllerTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;
    @MockitoBean PaymentService paymentService;

    private PaymentResponseDto successResponse() {
        return PaymentResponseDto.builder()
                .transactionId("txn-123")
                .status(PaymentStatus.SUCCESS)
                .message("Payment successful")
                .amount(BigDecimal.valueOf(199))
                .paymentMode(PaymentMode.UPI)
                .build();
    }

    // ── POST /api/payments/process ──────────────────────────────────

    @Test
    void processPayment_returns200() throws Exception {
        when(paymentService.processPayment(any())).thenReturn(successResponse());

        PaymentRequestDto req = PaymentRequestDto.builder()
                .userId(1L).amount(BigDecimal.valueOf(199)).paymentMode("UPI").build();

        mockMvc.perform(post("/api/payments/process")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.transactionId").value("txn-123"))
                .andExpect(jsonPath("$.status").value("SUCCESS"));
    }

    // ── GET /api/payments/{transactionId} ──────────────────────────

    @Test
    void getTransactionStatus_returns200() throws Exception {
        when(paymentService.getTransactionStatus("txn-123")).thenReturn(successResponse());

        mockMvc.perform(get("/api/payments/txn-123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.transactionId").value("txn-123"));
    }

    // ── GET /api/payments/user/{userId} ────────────────────────────

    @Test
    void getPaymentHistory_returns200() throws Exception {
        when(paymentService.getPaymentHistoryByUserId(1L)).thenReturn(List.of(successResponse()));

        mockMvc.perform(get("/api/payments/user/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void getPaymentHistory_empty_returns200() throws Exception {
        when(paymentService.getPaymentHistoryByUserId(99L)).thenReturn(List.of());

        mockMvc.perform(get("/api/payments/user/99"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }
}
