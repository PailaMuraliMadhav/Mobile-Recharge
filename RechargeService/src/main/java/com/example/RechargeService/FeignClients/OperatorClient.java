package com.example.RechargeService.FeignClients;

import com.example.RechargeService.Dto.OperatorResponse;
import com.example.RechargeService.Dto.PlanResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "OPERATOR-SERVICE")
public interface OperatorClient {

    // Get operator by ID
    @GetMapping("/api/operators/{id}")
    OperatorResponse getOperatorById(@PathVariable("id") Long id);

    // Get plan by planId
    @GetMapping("/api/operators/plans/{planId}")
    PlanResponse getPlanById(@PathVariable("planId") Long planId);
}