package com.example.PaymentService.Dto;


import com.example.PaymentService.Enums.PaymentStatus;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentResponseDto {
    private String message;

    private String transactionId;

    private PaymentStatus status;

    private BigDecimal amount;

}