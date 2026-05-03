package com.example.rechargeservice.controller;

import com.example.rechargeservice.dto.AdminAnalyticsResponse;
import com.example.rechargeservice.dto.PaymentVerificationUpdateRequest;
import com.example.rechargeservice.dto.RechargeRequest;
import com.example.rechargeservice.dto.RechargeResponse;
import com.example.rechargeservice.service.RechargeService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/*
 * AUTHOR: Paila Murali Madhav
 * CLASS: RechargeController
 * DESCRIPTION:
 *   REST controller that exposes endpoints for initiating mobile recharges,
 *   retrieving recharge history, and updating payment status after external verification.
 */
@RestController
@RequestMapping("/api/recharges")
@RequiredArgsConstructor
public class RechargeController {

    /** Payment mode value that indicates the user cancelled before payment. */
    private static final String CANCELLED_PAYMENT_MODE = "CANCELLED";

    private final RechargeService rechargeService;

    /* ================================================================
     * METHOD: recharge
     * DESCRIPTION:
     *   Initiates a mobile recharge for the given request. Cancelled payment attempts
     *   are routed to a dedicated method that records the attempt as FAILED immediately,
     *   bypassing the circuit breaker and payment service.
     * ================================================================ */
    @PostMapping
    public ResponseEntity<RechargeResponse> recharge(
            @Valid @RequestBody final RechargeRequest request) {

        if (CANCELLED_PAYMENT_MODE.equalsIgnoreCase(request.getPaymentMode())) {
            return ResponseEntity.ok(rechargeService.recordCancelledRecharge(request));
        }

        return ResponseEntity.ok(rechargeService.processRecharge(request));
    }

    /* ================================================================
     * METHOD: getRechargeHistory
     * DESCRIPTION:
     *   Returns the complete recharge history for a specific user identified by their user ID.
     * ================================================================ */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<RechargeResponse>> getRechargeHistory(
            @PathVariable final Long userId) {
        return ResponseEntity.ok(rechargeService.getRechargeHistory(userId));
    }

    /* ================================================================
     * METHOD: getAllRecharges
     * DESCRIPTION:
     *   Returns all recharge records across all users, intended for administrative use.
     * ================================================================ */
    @GetMapping("/admin/all")
    public ResponseEntity<List<RechargeResponse>> getAllRecharges() {
        return ResponseEntity.ok(rechargeService.getAllRecharges());
    }

    /* ================================================================
     * METHOD: updatePaymentStatus
     * DESCRIPTION:
     *   Updates the payment status of an existing recharge record after external
     *   payment verification, including transaction ID and payment mode.
     * ================================================================ */
    @PostMapping("/payment-status")
    public ResponseEntity<RechargeResponse> updatePaymentStatus(
            @Valid @RequestBody final PaymentVerificationUpdateRequest request) {
        return ResponseEntity.ok(rechargeService.updatePaymentStatus(request));
    }

    /* ================================================================
     * METHOD: getAdminAnalytics
     * DESCRIPTION:
     *   Returns aggregated analytics data for the admin dashboard.
     * ================================================================ */
    @GetMapping("/admin/analytics")
    public ResponseEntity<AdminAnalyticsResponse> getAdminAnalytics() {
        return ResponseEntity.ok(rechargeService.getAdminAnalytics());
    }
}
