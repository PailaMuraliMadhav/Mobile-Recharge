package com.example.PaymentService.Service;

import java.math.BigDecimal;

import com.example.PaymentService.Dto.PaymentRequestDto;
import com.example.PaymentService.Dto.PaymentResponseDto;
import com.example.PaymentService.Entity.Payment;
import com.example.PaymentService.Enums.PaymentStatus;
import com.example.PaymentService.Exception.NotFoundException;
import com.example.PaymentService.Repository.PaymentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PaymentServiceTest {

    @Mock
    private PaymentRepository paymentRepository;

    @InjectMocks
    private PaymentService paymentService;

    private PaymentRequestDto paymentRequest;
    private Payment dummyPayment;

    @BeforeEach
    void setUp() {
        paymentRequest = new PaymentRequestDto();
        paymentRequest.setRechargeId(101L);
        paymentRequest.setUserId(1L);
        paymentRequest.setAmount(BigDecimal.valueOf(50.0));

        dummyPayment = Payment.builder()
                .id(1L)
                .rechargeId(101L)
                .userId(1L)
                .amount(BigDecimal.valueOf(50.0))
                .status(PaymentStatus.SUCCESS)
                .transactionId("txn-12345")
                .build();
    }

    @Test
    void testProcessPayment_Success() {
        PaymentResponseDto response = paymentService.processPayment(paymentRequest);

        assertNotNull(response);
        assertEquals("Payment successful", response.getMessage());
        assertEquals(PaymentStatus.SUCCESS, response.getStatus());
        assertEquals(BigDecimal.valueOf(50.0), response.getAmount());
        assertNotNull(response.getTransactionId());

        ArgumentCaptor<Payment> paymentCaptor = ArgumentCaptor.forClass(Payment.class);
        verify(paymentRepository, times(1)).save(paymentCaptor.capture());

        Payment savedPayment = paymentCaptor.getValue();
        assertEquals(101L, savedPayment.getRechargeId());
        assertEquals(1L, savedPayment.getUserId());
        assertEquals(BigDecimal.valueOf(50.0), savedPayment.getAmount());
        assertEquals(PaymentStatus.SUCCESS, savedPayment.getStatus());
    }

    @Test
    void testGetTransactionStatus_Success() {
        when(paymentRepository.findByTransactionId("txn-12345")).thenReturn(Optional.of(dummyPayment));

        PaymentResponseDto response = paymentService.getTransactionStatus("txn-12345");

        assertNotNull(response);
        assertEquals("txn-12345", response.getTransactionId());
        assertEquals(PaymentStatus.SUCCESS, response.getStatus());
        assertEquals(BigDecimal.valueOf(50.0), response.getAmount());
    }

    @Test
    void testGetTransactionStatus_NotFound() {
        when(paymentRepository.findByTransactionId("invalid-txn")).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> paymentService.getTransactionStatus("invalid-txn"));
    }
}
