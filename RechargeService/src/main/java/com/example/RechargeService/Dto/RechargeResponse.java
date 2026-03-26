package com.example.RechargeService.Dto;

import com.example.RechargeService.Enums.RechargeStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RechargeResponse {

    private Long id;
    private Long userId;
    private Long operatorId;
    private Long planId;
    private String mobileNumber;
    private BigDecimal amount;
    private RechargeStatus status;
    private String transactionId;
    private LocalDateTime createdAt;
}
