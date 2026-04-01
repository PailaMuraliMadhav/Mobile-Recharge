package com.example.paymentservice.service;

import com.example.paymentservice.dto.PaymentRequestDto;
import com.example.paymentservice.dto.PaymentResponseDto;
import com.example.paymentservice.entity.Payment;
import com.example.paymentservice.enums.PaymentStatus;
import com.example.paymentservice.exception.NotFoundException;
import com.example.paymentservice.repository.PaymentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.modelmapper.ModelMapper;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT) // Prevents the stubbing errors we saw earlier
class PaymentServiceTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private PaymentService paymentService;

    private PaymentRequestDto requestDto;
    private Payment dummyPayment;
    private PaymentResponseDto responseDto;

    @BeforeEach
    void setUp() {
        requestDto = new PaymentRequestDto();
        requestDto.setRechargeId(100L);
        requestDto.setUserId(1L);
        requestDto.setAmount(BigDecimal.valueOf(199.00));

        dummyPayment = new Payment();
        dummyPayment.setId(10L);
        dummyPayment.setRechargeId(100L);
        dummyPayment.setUserId(1L);
        dummyPayment.setAmount(BigDecimal.valueOf(199.00));
        dummyPayment.setStatus(PaymentStatus.SUCCESS);
        dummyPayment.setTransactionId("txn-mock-123");

        responseDto = new PaymentResponseDto();
        responseDto.setTransactionId("txn-mock-123");
        responseDto.setStatus(PaymentStatus.SUCCESS);
        responseDto.setAmount(BigDecimal.valueOf(199.00));
    }

    @Test
    void testProcessPayment_Success() {
        when(paymentRepository.save(any(Payment.class))).thenReturn(dummyPayment);

        PaymentResponseDto result = paymentService.processPayment(requestDto);

        assertNotNull(result);
        assertEquals(PaymentStatus.SUCCESS, result.getStatus());
        assertEquals("Payment successful", result.getMessage());
        assertNotNull(result.getTransactionId()); // UUID is generated inside the method

        verify(paymentRepository, times(1)).save(any(Payment.class));
    }

    @Test
    void testGetTransactionStatus_Success() {
        when(paymentRepository.findByTransactionId("txn-mock-123")).thenReturn(Optional.of(dummyPayment));

        PaymentResponseDto result = paymentService.getTransactionStatus("txn-mock-123");

        assertNotNull(result);
        assertEquals("txn-mock-123", result.getTransactionId());
        assertEquals(PaymentStatus.SUCCESS, result.getStatus());
        assertEquals("Transaction fetched successfully", result.getMessage());
    }

    @Test
    void testGetTransactionStatus_NotFound() {
        when(paymentRepository.findByTransactionId("invalid-txn")).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> paymentService.getTransactionStatus("invalid-txn"));
    }

    @Test
    void testGetPaymentHistoryByUserId_Success() {
        when(paymentRepository.findByUserId(1L)).thenReturn(List.of(dummyPayment));
        when(modelMapper.map(any(Payment.class), eq(PaymentResponseDto.class))).thenReturn(responseDto);

        List<PaymentResponseDto> result = paymentService.getPaymentHistoryByUserId(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("txn-mock-123", result.get(0).getTransactionId());
    }
}