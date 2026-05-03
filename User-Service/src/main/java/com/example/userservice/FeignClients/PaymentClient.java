package com.example.userservice.feignclients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/*
 * AUTHOR: Paila Murali Madhav
 * CLASS: PaymentClient
 * DESCRIPTION:
 *   Feign client interface for inter-service HTTP communication with the Payment Service.
 *   Used by UserService to retrieve transaction status by transaction ID.
 */
@FeignClient(name = "payment-service")
public interface PaymentClient {

    /* ================================================================
     * METHOD: getTransactionStatus
     * DESCRIPTION:
     *   Retrieves the payment transaction status for the given transaction ID.
     * ================================================================ */
    @GetMapping("/api/payments/{transactionId}")
    Object getTransactionStatus(@PathVariable("transactionId") String transactionId);
}
