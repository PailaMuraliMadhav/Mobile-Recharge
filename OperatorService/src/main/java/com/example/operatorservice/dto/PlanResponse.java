package com.example.operatorservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor

public class PlanResponse {
    private Long id;
    private String name;
    private BigDecimal price;
    private Integer validityDays;
    private String data;
    private String description;
    private Boolean isActive;
    private Long operatorId;
    private String operatorName;
    private LocalDateTime createdAt;
}
