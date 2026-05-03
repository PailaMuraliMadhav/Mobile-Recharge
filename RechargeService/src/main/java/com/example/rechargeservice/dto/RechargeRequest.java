package com.example.rechargeservice.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

/*
 * AUTHOR: Paila Murali Madhav
 * CLASS: RechargeRequest
 * DESCRIPTION:
 *   Data Transfer Object representing an inbound mobile recharge request.
 *   Contains user ID, operator ID, plan ID, mobile number, and payment mode with validation constraints.
 */
@Data
public class RechargeRequest {

    @NotNull(message = "user id is required")
    private Long userId;

    @NotNull(message = "Operator ID is required")
    private Long operatorId;

    @NotNull(message = "Plan ID is required")
    private Long planId;

    @NotBlank(message = "Mobile number is required")
    @Size(min = 10, max = 10, message = "Mobile number must be 10 digits")
    @Pattern(
            regexp = "^[6-9]\\d{9}$",
            message = "Enter a valid 10-digit Indian mobile number"
    )
    private String mobileNumber;

    @NotBlank(message = "Payment mode is required")
    private String paymentMode;
}
