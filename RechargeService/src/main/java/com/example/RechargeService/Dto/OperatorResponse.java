package com.example.RechargeService.Dto;

import lombok.Data;

@Data
public class OperatorResponse {

    private Long id;
    private String name;
    private String code;
    private Boolean isActive;
}