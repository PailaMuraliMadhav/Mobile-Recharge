package com.example.NotificationService.Dto;


import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class RechargeEvent {

    private Long id;
    private Long userId;
    private Long operatorId;
    private Long planId;
    private String mobileNumber;
    private BigDecimal amount;
    private String status;
    private String transactionId;
    private LocalDateTime createdAt;
}