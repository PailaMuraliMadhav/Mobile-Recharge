package com.example.paymentservice.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentVerificationUpdateRequest {
    private Long rechargeId;
    private String transactionId;
    private String paymentMode;
    private String status;
}
