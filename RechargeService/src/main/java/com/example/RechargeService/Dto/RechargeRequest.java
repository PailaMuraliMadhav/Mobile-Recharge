package com.example.RechargeService.Dto;

import jakarta.validation.constraints.*;
import lombok.Data;

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
}