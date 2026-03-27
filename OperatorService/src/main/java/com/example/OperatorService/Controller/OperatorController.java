package com.example.OperatorService.Controller;

import com.example.OperatorService.Dto.OperatorResponse;
import com.example.OperatorService.Dto.PlanResponse;
import com.example.OperatorService.Service.OperatorService;
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