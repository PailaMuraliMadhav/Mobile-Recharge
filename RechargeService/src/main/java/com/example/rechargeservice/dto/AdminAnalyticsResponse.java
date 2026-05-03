package com.example.rechargeservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminAnalyticsResponse {
    private Map<String, Long> operatorUsage;
    private Map<String, Long> popularPlans;
    private Map<String, BigDecimal> revenueByDay;
    private Map<String, Long> paymentModeDistribution;
    private Long totalRecharges;
    private BigDecimal totalRevenue;
}
