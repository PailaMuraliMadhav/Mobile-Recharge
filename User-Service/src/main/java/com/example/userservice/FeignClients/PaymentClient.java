package com.example.userservice.feignclients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "payment-service")
public interface PaymentClient {
    @GetMapping("/api/payments/{transactionId}")
    Object getTransactionStatus(@PathVariable("transactionId")String transactionId);
}
