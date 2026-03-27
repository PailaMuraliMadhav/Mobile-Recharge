package com.example.PaymentService.Service;

import com.example.PaymentService.Dto.PaymentRequestDto;
import com.example.PaymentService.Dto.PaymentResponseDto;
import com.example.PaymentService.Entity.Payment;
import com.example.PaymentService.Enums.PaymentStatus;
import com.example.PaymentService.Exception.NotFoundException;
import com.example.PaymentService.Repository.PaymentRepository;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final ModelMapper modelMapper;

    public PaymentResponseDto processPayment(PaymentRequestDto request) {

        String transactionId = UUID.randomUUID().toString();

        Payment payment = Payment.builder()
                .rechargeId(request.getRechargeId())
                .userId(request.getUserId())
                .amount(request.getAmount())
                .status(PaymentStatus.SUCCESS)
                .transactionId(transactionId)
                .build();

        paymentRepository.save(payment);

        return PaymentResponseDto.builder()
                .message("Payment successful")
                .transactionId(transactionId)
                .status(PaymentStatus.SUCCESS)
                .amount(request.getAmount())
                .build();
    }

    public PaymentResponseDto getTransactionStatus(String transactionId) {

        Payment payment = paymentRepository
                .findByTransactionId(transactionId)
                .orElseThrow(() ->
                        new NotFoundException("Transaction not found"));

        return PaymentResponseDto.builder()
                .message("Transaction fetched successfully")
                .transactionId(payment.getTransactionId())
                .status(payment.getStatus())
                .amount(payment.getAmount())
                .build();
    }
    // Inside PaymentService.java

    public List<PaymentResponseDto> getPaymentHistoryByUserId(Long userId) {
        return paymentRepository.findByUserId(userId)
                .stream()
                .map(payment -> modelMapper.map(payment, PaymentResponseDto.class))
                .toList();
    }

}