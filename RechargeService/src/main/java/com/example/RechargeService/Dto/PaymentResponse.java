package com.example.RechargeService.Dto;


import com.example.RechargeService.Enums.RechargeStatus;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class PaymentResponse {

    private String message;

    private String transactionId;

    private RechargeStatus status;

    private BigDecimal amount;

}