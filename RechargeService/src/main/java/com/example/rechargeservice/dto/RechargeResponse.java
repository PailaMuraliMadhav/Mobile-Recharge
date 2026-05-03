package com.example.rechargeservice.dto;

import com.example.rechargeservice.enums.RechargeStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/*
 * AUTHOR: Paila Murali Madhav
 * CLASS: RechargeResponse
 * DESCRIPTION:
 *   Data Transfer Object representing an outbound recharge response.
 *   Contains all recharge details including status, transaction ID, and timestamps.
 */
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
    private String paymentMode;
    private String transactionId;
    private LocalDateTime createdAt;
}
