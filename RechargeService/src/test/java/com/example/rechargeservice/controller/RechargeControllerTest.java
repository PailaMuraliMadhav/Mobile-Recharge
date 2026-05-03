package com.example.rechargeservice.controller;

import com.example.rechargeservice.dto.PaymentVerificationUpdateRequest;
import com.example.rechargeservice.dto.RechargeRequest;
import com.example.rechargeservice.dto.RechargeResponse;
import com.example.rechargeservice.enums.RechargeStatus;
import com.example.rechargeservice.service.RechargeService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/*
 * AUTHOR: Paila Murali Madhav
 * CLASS: RechargeControllerTest
 * DESCRIPTION:
 *   Unit tests for RechargeController covering all recharge endpoints.
 */
@WebMvcTest(RechargeController.class)
@AutoConfigureMockMvc(addFilters = false)
class RechargeControllerTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;
    @MockitoBean RechargeService rechargeService;

    private RechargeResponse successRecharge() {
        RechargeResponse r = new RechargeResponse();
        r.setId(1L);
        r.setMobileNumber("9876543210");
        r.setStatus(RechargeStatus.SUCCESS);
        r.setAmount(BigDecimal.valueOf(199));
        r.setTransactionId("TXN-001");
        return r;
    }

    // ── POST /api/recharges — normal payment ────────────────────────

    @Test
    void recharge_normalPayment_returns200() throws Exception {
        when(rechargeService.processRecharge(any())).thenReturn(successRecharge());

        RechargeRequest req = new RechargeRequest();
        req.setUserId(1L); req.setOperatorId(10L); req.setPlanId(100L);
        req.setMobileNumber("9876543210"); req.setPaymentMode("UPI");

        mockMvc.perform(post("/api/recharges")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.transactionId").value("TXN-001"));
    }

    // ── POST /api/recharges — CANCELLED payment mode ────────────────

    @Test
    void recharge_cancelledPayment_callsRecordCancelled() throws Exception {
        RechargeResponse cancelled = new RechargeResponse();
        cancelled.setId(2L);
        cancelled.setStatus(RechargeStatus.FAILED);
        cancelled.setMobileNumber("9876543210");
        when(rechargeService.recordCancelledRecharge(any())).thenReturn(cancelled);

        RechargeRequest req = new RechargeRequest();
        req.setUserId(1L); req.setOperatorId(10L); req.setPlanId(100L);
        req.setMobileNumber("9876543210"); req.setPaymentMode("CANCELLED");

        mockMvc.perform(post("/api/recharges")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("FAILED"));
    }

    // ── GET /api/recharges/user/{userId} ────────────────────────────

    @Test
    void getRechargeHistory_returns200() throws Exception {
        when(rechargeService.getRechargeHistory(1L)).thenReturn(List.of(successRecharge()));

        mockMvc.perform(get("/api/recharges/user/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].mobileNumber").value("9876543210"));
    }

    @Test
    void getRechargeHistory_empty_returns200() throws Exception {
        when(rechargeService.getRechargeHistory(99L)).thenReturn(List.of());

        mockMvc.perform(get("/api/recharges/user/99"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    // ── GET /api/recharges ──────────────────────────────────────────

    @Test
    void getAllRecharges_returns200() throws Exception {
        when(rechargeService.getAllRecharges()).thenReturn(List.of(successRecharge()));

        mockMvc.perform(get("/api/recharges"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    // ── POST /api/recharges/payment-status ─────────────────────────

    @Test
    void updatePaymentStatus_returns200() throws Exception {
        when(rechargeService.updatePaymentStatus(any())).thenReturn(successRecharge());

        PaymentVerificationUpdateRequest req = new PaymentVerificationUpdateRequest();
        req.setRechargeId(1L); req.setTransactionId("TXN-001");
        req.setStatus("SUCCESS"); req.setPaymentMode("UPI");

        mockMvc.perform(post("/api/recharges/payment-status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.transactionId").value("TXN-001"));
    }
}
