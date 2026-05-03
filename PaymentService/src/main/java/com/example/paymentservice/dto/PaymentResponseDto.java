package com.example.paymentservice.dto;

import com.example.paymentservice.enums.PaymentMode;
import com.example.paymentservice.enums.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/*
 * AUTHOR: Paila Murali Madhav
 * CLASS: PaymentResponseDto
 * DESCRIPTION:
 *   Data Transfer Object representing an outbound payment response.
 *   Contains the transaction ID, status, payment mode, amount, and a descriptive message.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentResponseDto {
    private String message;
    private String transactionId;
    private PaymentStatus status;
    private PaymentMode paymentMode;
    private BigDecimal amount;
}
