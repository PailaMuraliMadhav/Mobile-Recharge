package com.example.paymentservice.feignclients;

import com.example.paymentservice.dto.PaymentVerificationUpdateRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "RECHARGE-SERVICE")
public interface RechargeClient {

    @PostMapping("/api/recharges/payment-status")
    void updatePaymentStatus(@RequestBody PaymentVerificationUpdateRequest request);
}
