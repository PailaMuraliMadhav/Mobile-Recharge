package com.example.rechargeservice.feignclients;

import com.example.rechargeservice.dto.PaymentRequest;
import com.example.rechargeservice.dto.PaymentResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/*
 * AUTHOR: Paila Murali Madhav
 * CLASS: PaymentClient
 * DESCRIPTION:
 *   Feign client interface for inter-service HTTP communication with the Payment Service.
 *   Used by RechargeService to trigger payment processing after a recharge is created.
 */
@FeignClient(name = "PAYMENT-SERVICE")
public interface PaymentClient {

    /* ================================================================
     * METHOD: processPayment
     * DESCRIPTION:
     *   Sends a payment request to the Payment Service and returns the payment result.
     * ================================================================ */
    @PostMapping("/api/payments/process")
    PaymentResponse processPayment(@RequestBody PaymentRequest request);

}
