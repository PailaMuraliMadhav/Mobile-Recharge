package com.example.operatorservice.controller;

import com.example.operatorservice.dto.OperatorResponse;
import com.example.operatorservice.dto.PlanResponse;
import com.example.operatorservice.service.OperatorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/operators")
@RequiredArgsConstructor
public class OperatorController {

    private final OperatorService operatorService;

    // Get all operators
    @GetMapping
    public ResponseEntity<List<OperatorResponse>> getAllOperators() {
        return ResponseEntity.ok(operatorService.getAllOperators());
    }

    // Get operator by id
    @GetMapping("/{id}")
    public ResponseEntity<OperatorResponse> getOperatorById(@PathVariable Long id) {
        return ResponseEntity.ok(operatorService.getOperatorById(id));
    }

    // Get plans by operator
    @GetMapping("/{operatorId}/plans")
    public ResponseEntity<List<PlanResponse>> getPlansByOperator(
            @PathVariable Long operatorId) {
        return ResponseEntity.ok(operatorService.getPlansByOperator(operatorId));
    }
    @GetMapping("/plans/{planId}")
    public ResponseEntity<PlanResponse> getPlanById(@PathVariable Long planId) {
        return ResponseEntity.ok(operatorService.getPlanById(planId));
    }
}