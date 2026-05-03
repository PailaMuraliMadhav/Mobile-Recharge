package com.example.rechargeservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/*
 * AUTHOR: Paila Murali Madhav
 * CLASS: PaymentVerificationUpdateRequest
 * DESCRIPTION:
 *   Request DTO for updating a recharge record after external payment verification.
 *   Contains the recharge ID, transaction ID, payment mode, and final status.
 */
@Data
public class PaymentVerificationUpdateRequest {

    @NotNull(message = "Recharge ID is required")
    private Long rechargeId;

    @NotBlank(message = "Transaction ID is required")
    private String transactionId;

    @NotBlank(message = "Payment mode is required")
    private String paymentMode;

    @NotBlank(message = "Payment status is required")
    private String status;
}
