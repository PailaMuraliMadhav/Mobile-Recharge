package com.example.paymentservice.controller;


import com.example.paymentservice.dto.PaymentRequestDto;
import com.example.paymentservice.dto.PaymentResponseDto;
import com.example.paymentservice.service.PaymentService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/process")
    public ResponseEntity<PaymentResponseDto> processPayment(
            @Valid @RequestBody PaymentRequestDto request) {

        return ResponseEntity.ok(
                paymentService.processPayment(request)
        );
    }

    @GetMapping("/{transactionId}")
    public ResponseEntity<PaymentResponseDto> getTransactionStatus(
            @PathVariable String transactionId) {

        return ResponseEntity.ok(
                paymentService.getTransactionStatus(transactionId)
        );
    }
    // Inside PaymentController.java

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<PaymentResponseDto>> getHistory(@PathVariable Long userId) {
        List<PaymentResponseDto> history = paymentService.getPaymentHistoryByUserId(userId);
        return ResponseEntity.ok(history);
    }
}