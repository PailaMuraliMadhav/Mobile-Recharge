package com.example.rechargeservice.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class PlanResponse {

    private Long id;
    private String name;
    private BigDecimal price;
    private Integer validityDays;
    private String data;
    private Boolean isActive;
    private Long operatorId;
}