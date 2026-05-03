package com.example.paymentservice.service;

import com.example.paymentservice.dto.PaymentRequestDto;
import com.example.paymentservice.dto.PaymentResponseDto;
import com.example.paymentservice.entity.Payment;
import com.example.paymentservice.enums.PaymentMode;
import com.example.paymentservice.enums.PaymentStatus;
import com.example.paymentservice.exception.NotFoundException;
import com.example.paymentservice.repository.PaymentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/*
 * AUTHOR: Paila Murali Madhav
 * CLASS: PaymentServiceTest
 * DESCRIPTION:
 *   Unit tests for PaymentService covering standard gateway modes,
 *   transaction status lookup, and payment history.
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class PaymentServiceTest {

    @Mock private PaymentRepository paymentRepository;

    @InjectMocks private PaymentService paymentService;

    private PaymentRequestDto requestDto;
    private Payment dummyPayment;

    @BeforeEach
    void setUp() {
        requestDto = PaymentRequestDto.builder()
                .rechargeId(100L)
                .userId(1L)
                .amount(BigDecimal.valueOf(199.00))
                .paymentMode("UPI")
                .description("Test recharge")
                .build();

        dummyPayment = Payment.builder()
                .id(10L)
                .rechargeId(100L)
                .userId(1L)
                .amount(BigDecimal.valueOf(199.00))
                .status(PaymentStatus.SUCCESS)
                .paymentMode(PaymentMode.UPI)
                .transactionId("txn-mock-123")
                .build();
    }

    // ════════════════════════════════════════════════════════════════
    // processPayment — gateway modes
    // ════════════════════════════════════════════════════════════════

    @Test
    void processPayment_upi_success() {
        when(paymentRepository.save(any(Payment.class))).thenReturn(dummyPayment);

        PaymentResponseDto result = paymentService.processPayment(requestDto);

        assertNotNull(result);
        assertEquals(PaymentStatus.SUCCESS, result.getStatus());
        assertEquals("Payment successful", result.getMessage());
        assertNotNull(result.getTransactionId());
        verify(paymentRepository).save(any(Payment.class));
    }

    @ParameterizedTest
    @ValueSource(strings = {"UPI", "CARD", "NETBANKING"})
    void processPayment_gatewayModes_success(String mode) {
        requestDto.setPaymentMode(mode);
        when(paymentRepository.save(any(Payment.class))).thenReturn(dummyPayment);

        PaymentResponseDto result = paymentService.processPayment(requestDto);

        assertEquals(PaymentStatus.SUCCESS, result.getStatus());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"  ", "CRYPTO"})
    void processPayment_invalidMode_throwsException(String mode) {
        requestDto.setPaymentMode(mode);

        assertThrows(IllegalArgumentException.class,
                () -> paymentService.processPayment(requestDto));
    }

    // ════════════════════════════════════════════════════════════════
    // getTransactionStatus
    // ════════════════════════════════════════════════════════════════

    @Test
    void getTransactionStatus_success() {
        when(paymentRepository.findByTransactionId("txn-mock-123"))
                .thenReturn(Optional.of(dummyPayment));

        PaymentResponseDto result = paymentService.getTransactionStatus("txn-mock-123");

        assertEquals("txn-mock-123", result.getTransactionId());
        assertEquals(PaymentStatus.SUCCESS, result.getStatus());
        assertEquals("Transaction fetched successfully", result.getMessage());
    }

    @Test
    void getTransactionStatus_notFound() {
        when(paymentRepository.findByTransactionId("bad-txn"))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> paymentService.getTransactionStatus("bad-txn"));
    }

    // ════════════════════════════════════════════════════════════════
    // getPaymentHistoryByUserId
    // ════════════════════════════════════════════════════════════════

    @Test
    void getPaymentHistory_success() {
        when(paymentRepository.findByUserId(1L)).thenReturn(List.of(dummyPayment));

        List<PaymentResponseDto> result = paymentService.getPaymentHistoryByUserId(1L);

        assertEquals(1, result.size());
        assertEquals("txn-mock-123", result.get(0).getTransactionId());
    }

    @Test
    void getPaymentHistory_empty() {
        when(paymentRepository.findByUserId(99L)).thenReturn(List.of());

        List<PaymentResponseDto> result = paymentService.getPaymentHistoryByUserId(99L);

        assertTrue(result.isEmpty());
    }
}
