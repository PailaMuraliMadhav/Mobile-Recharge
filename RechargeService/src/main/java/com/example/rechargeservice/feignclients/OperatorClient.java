package com.example.rechargeservice.feignclients;

import com.example.rechargeservice.dto.OperatorResponse;
import com.example.rechargeservice.dto.PlanResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/*
 * AUTHOR: Paila Murali Madhav
 * CLASS: OperatorClient
 * DESCRIPTION:
 *   Feign client interface for inter-service HTTP communication with the Operator Service.
 *   Used by RechargeService to fetch operator details and plan information before processing a recharge.
 */
@FeignClient(name = "OPERATOR-SERVICE")
public interface OperatorClient {

    /* ================================================================
     * METHOD: getOperatorById
     * DESCRIPTION:
     *   Retrieves operator details by operator ID from the Operator Service.
     * ================================================================ */
    @GetMapping("/api/operators/{id}")
    OperatorResponse getOperatorById(@PathVariable("id") Long id);

    /* ================================================================
     * METHOD: getPlanById
     * DESCRIPTION:
     *   Retrieves plan details by plan ID from the Operator Service.
     * ================================================================ */
    @GetMapping("/api/operators/plans/{planId}")
    PlanResponse getPlanById(@PathVariable("planId") Long planId);
}
