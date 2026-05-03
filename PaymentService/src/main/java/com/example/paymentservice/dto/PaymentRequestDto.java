package com.example.paymentservice.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/*
 * AUTHOR: Paila Murali Madhav
 * CLASS: PaymentRequestDto
 * DESCRIPTION:
 *   Data Transfer Object representing an inbound payment processing request.
 *   Contains recharge ID, user ID, amount, payment mode, and description with validation constraints.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentRequestDto {

    private Long rechargeId;

    @NotNull(message = "User ID is required")
    private Long userId;

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "1.0", message = "Amount must be greater than 0")
    private BigDecimal amount;

    @NotNull(message = "Payment mode is required")
    private String paymentMode;

    private String description;
}
