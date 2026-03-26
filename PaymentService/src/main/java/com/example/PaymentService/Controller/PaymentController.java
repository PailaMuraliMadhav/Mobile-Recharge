package com.example.PaymentService.Controller;


import com.example.PaymentService.Dto.PaymentRequestDto;
import com.example.PaymentService.Dto.PaymentResponseDto;
import com.example.PaymentService.Service.PaymentService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
}