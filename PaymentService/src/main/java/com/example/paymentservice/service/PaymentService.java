package com.example.paymentservice.service;

import com.example.paymentservice.dto.*;
import com.example.paymentservice.entity.Payment;
import com.example.paymentservice.enums.PaymentMode;
import com.example.paymentservice.enums.PaymentStatus;
import com.example.paymentservice.exception.NotFoundException;
import com.example.paymentservice.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/*
 * AUTHOR: Paila Murali Madhav
 * CLASS: PaymentService
 * DESCRIPTION:
 *   Service for standard payment processing.
 *   Handles UPI / CARD / NETBANKING mock payments.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {

    private final PaymentRepository paymentRepository;

    // ─────────────────────────────────────────────────────────────────────────
    // PAYMENT PROCESSING
    // ─────────────────────────────────────────────────────────────────────────

    /* ================================================================
     * METHOD: processPayment
     * DESCRIPTION:
     *   Entry point for all payment requests.
     *   Processes UPI, CARD, and NETBANKING as mock gateway payments.
     * ================================================================ */
    @Transactional
    public PaymentResponseDto processPayment(PaymentRequestDto request) {
        if (request.getPaymentMode() == null || request.getPaymentMode().isBlank()) {
            throw new IllegalArgumentException("Payment mode is required");
        }

        PaymentMode mode;
        try {
            mode = PaymentMode.valueOf(request.getPaymentMode().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid payment mode: " + request.getPaymentMode());
        }

        return processGatewayPayment(request, mode);
    }

    /* ================================================================
     * METHOD: processGatewayPayment
     * DESCRIPTION:
     *   Processes a UPI / CARD / NETBANKING payment (mock success).
     *   Persists a Payment record and returns a SUCCESS response.
     * ================================================================ */
    private PaymentResponseDto processGatewayPayment(PaymentRequestDto request, PaymentMode mode) {
        String transactionId = UUID.randomUUID().toString();

        Payment payment = Payment.builder()
                .rechargeId(request.getRechargeId())
                .userId(request.getUserId())
                .amount(request.getAmount())
                .status(PaymentStatus.SUCCESS)
                .paymentMode(mode)
                .transactionId(transactionId)
                .description(request.getDescription())
                .build();

        paymentRepository.save(payment);
        log.info("Gateway payment processed. txn={}, mode={}, amount=Rs.{}", transactionId, mode, request.getAmount());

        return PaymentResponseDto.builder()
                .message("Payment successful")
                .transactionId(transactionId)
                .status(PaymentStatus.SUCCESS)
                .paymentMode(mode)
                .amount(request.getAmount())
                .build();
    }

    /* ================================================================
     * METHOD: getTransactionStatus
     * DESCRIPTION:
     *   Retrieves a payment record by its transaction ID.
     * ================================================================ */
    public PaymentResponseDto getTransactionStatus(String transactionId) {
        Payment payment = paymentRepository.findByTransactionId(transactionId)
                .orElseThrow(() -> new NotFoundException("Transaction not found: " + transactionId));

        return PaymentResponseDto.builder()
                .message("Transaction fetched successfully")
                .transactionId(payment.getTransactionId())
                .status(payment.getStatus())
                .paymentMode(payment.getPaymentMode())
                .amount(payment.getAmount())
                .build();
    }

    /* ================================================================
     * METHOD: getPaymentHistoryByUserId
     * DESCRIPTION:
     *   Returns all payment records for a given user.
     * ================================================================ */
    public List<PaymentResponseDto> getPaymentHistoryByUserId(Long userId) {
        return paymentRepository.findByUserId(userId).stream()
                .map(p -> PaymentResponseDto.builder()
                        .transactionId(p.getTransactionId())
                        .status(p.getStatus())
                        .paymentMode(p.getPaymentMode())
                        .amount(p.getAmount())
                        .message("Payment record")
                        .build())
                .toList();
    }
}
