package com.example.paymentservice.controller;

import com.example.paymentservice.dto.*;
import com.example.paymentservice.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
@Tag(name = "Payments", description = "Payment processing management")
public class PaymentController {

    private final PaymentService paymentService;

    /* ================================================================
     * METHOD: createRazorpayOrder
     * DESCRIPTION:
     *   Creates an order in Razorpay for a recharge.
     * ================================================================ */
    @PostMapping("/razorpay/create-order")
    @Operation(summary = "Create a Razorpay order")
    public ResponseEntity<RazorpayOrderResponse> createRazorpayOrder(
            @RequestBody RazorpayOrderRequest request) {
        return ResponseEntity.ok(paymentService.createRazorpayOrder(request));
    }

    /* ================================================================
     * METHOD: verifyRazorpayPayment
     * DESCRIPTION:
     *   Verifies a Razorpay payment signature.
     * ================================================================ */
    @PostMapping("/razorpay/verify")
    @Operation(summary = "Verify a Razorpay payment")
    public ResponseEntity<PaymentResponseDto> verifyRazorpayPayment(
            @RequestBody RazorpayVerifyRequest request) {
        return ResponseEntity.ok(paymentService.verifyRazorpayPayment(request));
    }

    /* ================================================================
     * METHOD: processPayment
     * DESCRIPTION:
     *   Processes a payment for a recharge.
     *   Supported modes: UPI, CARD, NETBANKING.
     * ================================================================ */
    @PostMapping("/process")
    @Operation(summary = "Process a payment")
    public ResponseEntity<PaymentResponseDto> processPayment(
            @Valid @RequestBody PaymentRequestDto request) {
        return ResponseEntity.ok(paymentService.processPayment(request));
    }

    /* ================================================================
     * METHOD: getTransactionStatus
     * DESCRIPTION:
     *   Returns the status and details of a payment by transaction ID.
     * ================================================================ */
    @GetMapping("/{transactionId}")
    @Operation(summary = "Get transaction status")
    public ResponseEntity<PaymentResponseDto> getTransactionStatus(
            @PathVariable String transactionId) {
        return ResponseEntity.ok(paymentService.getTransactionStatus(transactionId));
    }

    /* ================================================================
     * METHOD: getPaymentHistory
     * DESCRIPTION:
     *   Returns all payment records for a given user.
     * ================================================================ */
    @GetMapping("/user/{userId}")
    @Operation(summary = "Get payment history for a user")
    public ResponseEntity<List<PaymentResponseDto>> getPaymentHistory(
            @PathVariable Long userId) {
        return ResponseEntity.ok(paymentService.getPaymentHistoryByUserId(userId));
    }
}
