package com.example.paymentservice.service;

import com.example.paymentservice.dto.*;
import com.example.paymentservice.entity.Payment;
import com.example.paymentservice.enums.PaymentMode;
import com.example.paymentservice.enums.PaymentStatus;
import com.example.paymentservice.exception.NotFoundException;
import com.example.paymentservice.repository.PaymentRepository;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.Utils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
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
    private final RazorpayClient razorpayClient;
    private final com.example.paymentservice.feignclients.RechargeClient rechargeClient;

    @Value("${razorpay.key.secret}")
    private String keySecret;

    // ─────────────────────────────────────────────────────────────────────────
    // PAYMENT PROCESSING
    // ─────────────────────────────────────────────────────────────────────────

    /* ================================================================
     * METHOD: processPayment
     * DESCRIPTION:
     *   Entry point for all payment requests.
     *   Processes UPI, CARD, and NETBANKING as mock gateway payments.
     *   Processes RAZORPAY by creating a real Razorpay Order.
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

        if (mode == PaymentMode.RAZORPAY) {
            return processRazorpayOrder(request);
        }

        return processGatewayPayment(request, mode);
    }

    private PaymentResponseDto processRazorpayOrder(PaymentRequestDto request) {
        try {
            JSONObject orderRequest = new JSONObject();
            orderRequest.put("amount", request.getAmount().multiply(new java.math.BigDecimal(100)).intValue()); // Paisa
            orderRequest.put("currency", "INR");
            orderRequest.put("receipt", "txn_" + UUID.randomUUID().toString().substring(0, 8));

            Order order = razorpayClient.orders.create(orderRequest);
            String orderId = order.get("id");

            Payment payment = Payment.builder()
                    .rechargeId(request.getRechargeId())
                    .userId(request.getUserId())
                    .amount(request.getAmount())
                    .status(PaymentStatus.PENDING)
                    .paymentMode(PaymentMode.RAZORPAY)
                    .razorpayOrderId(orderId)
                    .transactionId(orderId) // Set as placeholder for NOT NULL constraint
                    .description(request.getDescription())
                    .build();

            paymentRepository.save(payment);

            return PaymentResponseDto.builder()
                    .message("Order created")
                    .razorpayOrderId(orderId)
                    .status(PaymentStatus.PENDING)
                    .paymentMode(PaymentMode.RAZORPAY)
                    .amount(request.getAmount())
                    .build();

        } catch (Exception e) {
            log.error("Error creating Razorpay order: {}", e.getMessage());
            throw new RuntimeException("Could not create Razorpay order");
        }
    }

    @Transactional
    public PaymentResponseDto verifyRazorpayPayment(RazorpayVerificationRequest request) {
        try {
            JSONObject attributes = new JSONObject();
            attributes.put("razorpay_order_id", request.getRazorpayOrderId());
            attributes.put("razorpay_payment_id", request.getRazorpayPaymentId());
            attributes.put("razorpay_signature", request.getRazorpaySignature());

            boolean isValid = Utils.verifyPaymentSignature(attributes, keySecret);

            if (!isValid) {
                throw new RuntimeException("Invalid Razorpay signature");
            }

            Payment payment = paymentRepository.findByRazorpayOrderId(request.getRazorpayOrderId())
                    .orElseThrow(() -> new NotFoundException("Order not found: " + request.getRazorpayOrderId()));

            payment.setStatus(PaymentStatus.SUCCESS);
            payment.setRazorpayPaymentId(request.getRazorpayPaymentId());
            payment.setRazorpaySignature(request.getRazorpaySignature());
            payment.setTransactionId(request.getRazorpayPaymentId()); // Use payment_id as transaction_id
            paymentRepository.save(payment);

            // Notify Recharge Service
            try {
                rechargeClient.updatePaymentStatus(PaymentVerificationUpdateRequest.builder()
                        .rechargeId(payment.getRechargeId())
                        .transactionId(payment.getTransactionId())
                        .paymentMode("RAZORPAY")
                        .status("SUCCESS")
                        .build());
            } catch (Exception e) {
                log.error("Failed to update recharge status: {}", e.getMessage());
            }

            return PaymentResponseDto.builder()
                    .message("Payment verified successfully")
                    .transactionId(payment.getTransactionId())
                    .status(PaymentStatus.SUCCESS)
                    .paymentMode(PaymentMode.RAZORPAY)
                    .amount(payment.getAmount())
                    .build();

        } catch (Exception e) {
            log.error("Payment verification failed: {}", e.getMessage());
            throw new RuntimeException("Payment verification failed");
        }
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
