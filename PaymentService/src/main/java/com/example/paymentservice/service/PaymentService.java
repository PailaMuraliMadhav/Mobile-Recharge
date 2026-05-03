package com.example.paymentservice.service;

import com.example.paymentservice.dto.*;
import com.example.paymentservice.entity.Payment;
import com.example.paymentservice.enums.PaymentMode;
import com.example.paymentservice.enums.PaymentStatus;
import com.example.paymentservice.exception.NotFoundException;
import com.example.paymentservice.repository.PaymentRepository;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import com.razorpay.Utils;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
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

    @Value("${razorpay.key.id}")
    private String razorpayKeyId;

    @Value("${razorpay.key.secret}")
    private String razorpayKeySecret;

    private RazorpayClient razorpayClient;

    @PostConstruct
    public void init() throws RazorpayException {
        this.razorpayClient = new RazorpayClient(razorpayKeyId, razorpayKeySecret);
    }

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

    // ─────────────────────────────────────────────────────────────────────────
    // RAZORPAY INTEGRATION
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Creates a Razorpay Order.
     */
    public RazorpayOrderResponse createRazorpayOrder(RazorpayOrderRequest request) {
        try {
            JSONObject orderRequest = new JSONObject();
            // Razorpay expects amount in paise (1 INR = 100 paise)
            int amountInPaise = request.getAmount().multiply(new BigDecimal(100)).intValue();
            
            orderRequest.put("amount", amountInPaise);
            orderRequest.put("currency", request.getCurrency() != null ? request.getCurrency() : "INR");
            orderRequest.put("receipt", "recharge_" + request.getRechargeId());
            
            Order order = razorpayClient.orders.create(orderRequest);
            
            log.info("Razorpay order created: {} for recharge: {}", order.get("id"), request.getRechargeId());
            
            return RazorpayOrderResponse.builder()
                    .orderId(order.get("id"))
                    .keyId(razorpayKeyId)
                    .amount(amountInPaise)
                    .currency(order.get("currency"))
                    .build();
                    
        } catch (RazorpayException e) {
            log.error("Error creating Razorpay order: {}", e.getMessage());
            throw new RuntimeException("Failed to create Razorpay order: " + e.getMessage());
        }
    }

    /**
     * Verifies the Razorpay payment signature and saves the payment record.
     */
    @Transactional
    public PaymentResponseDto verifyRazorpayPayment(RazorpayVerifyRequest request) {
        try {
            // Verify Signature
            JSONObject options = new JSONObject();
            options.put("razorpay_order_id", request.getRazorpayOrderId());
            options.put("razorpay_payment_id", request.getRazorpayPaymentId());
            options.put("razorpay_signature", request.getRazorpaySignature());

            boolean isValid = Utils.verifyPaymentSignature(options, razorpayKeySecret);

            if (!isValid) {
                log.error("Invalid Razorpay signature for order: {}", request.getRazorpayOrderId());
                throw new RuntimeException("Invalid payment signature");
            }

            // If valid, save the payment record
            // Fetch payment details from Razorpay to get the amount if needed, 
            // but here we trust the backend-to-backend verification.
            
            com.razorpay.Payment rzpPayment = razorpayClient.payments.fetch(request.getRazorpayPaymentId());
            BigDecimal amount = new BigDecimal(rzpPayment.get("amount").toString()).divide(new BigDecimal(100));

            Payment payment = Payment.builder()
                    .rechargeId(request.getRechargeId())
                    .userId(request.getUserId())
                    .amount(amount)
                    .status(PaymentStatus.SUCCESS)
                    .paymentMode(PaymentMode.ONLINE) // or specific mode if identifiable
                    .transactionId(request.getRazorpayPaymentId())
                    .description("Razorpay Payment for Recharge #" + request.getRechargeId())
                    .build();

            paymentRepository.save(payment);
            log.info("Razorpay payment verified and saved. txn={}", request.getRazorpayPaymentId());

            return PaymentResponseDto.builder()
                    .message("Payment verified successfully")
                    .transactionId(request.getRazorpayPaymentId())
                    .status(PaymentStatus.SUCCESS)
                    .paymentMode(PaymentMode.ONLINE)
                    .amount(amount)
                    .build();

        } catch (RazorpayException e) {
            log.error("Error verifying Razorpay payment: {}", e.getMessage());
            throw new RuntimeException("Failed to verify Razorpay payment: " + e.getMessage());
        }
    }
}
