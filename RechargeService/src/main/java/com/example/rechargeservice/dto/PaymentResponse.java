package com.example.rechargeservice.dto;


import com.example.rechargeservice.enums.RechargeStatus;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class PaymentResponse {

    private String message;

    private String transactionId;

    private RechargeStatus status;

    private BigDecimal amount;

}