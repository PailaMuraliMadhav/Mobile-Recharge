package com.example.paymentservice.service;

import com.example.paymentservice.dto.PaymentRequestDto;
import com.example.paymentservice.dto.PaymentResponseDto;
import com.example.paymentservice.entity.Payment;
import com.example.paymentservice.enums.PaymentStatus;
import com.example.paymentservice.exception.NotFoundException;
import com.example.paymentservice.repository.PaymentRepository;

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