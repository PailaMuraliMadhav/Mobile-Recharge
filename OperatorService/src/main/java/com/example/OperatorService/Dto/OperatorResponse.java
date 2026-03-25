package com.example.OperatorService.Dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OperatorResponse {
    private Long id;
    private String name;
    private String code;
    private String description;
    private Boolean isActive;
    private LocalDateTime createdAt;
}
