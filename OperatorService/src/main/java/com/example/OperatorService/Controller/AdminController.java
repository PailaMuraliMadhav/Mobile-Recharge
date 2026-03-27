package com.example.OperatorService.Controller;

import com.example.OperatorService.Dto.*;
import com.example.OperatorService.Service.OperatorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/operators")
@RequiredArgsConstructor
public class AdminController {

    private final OperatorService operatorService;

    // Add operator
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<OperatorResponse> addOperator(
            @Valid @RequestBody OperatorRequest request) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(operatorService.addOperator(request));
    }

    // Update operator
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<OperatorResponse> updateOperator(
            @PathVariable Long id,
            @Valid @RequestBody OperatorRequest request) {

        return ResponseEntity.ok(operatorService.updateOperator(id, request));
    }

    // Delete operator
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deleteOperator(@PathVariable Long id) {

        return ResponseEntity.ok(operatorService.deleteOperator(id));
    }

    // Add plan
    @PostMapping("/{operatorId}/plans")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PlanResponse> addPlan(
            @PathVariable Long operatorId,
            @Valid @RequestBody PlanRequest request) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(operatorService.addPlan(operatorId, request));
    }

    // Update plan
    @PatchMapping("/plans/{planId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PlanResponse> updatePlan(
            @PathVariable Long planId,
            @Valid @RequestBody PlanRequest request) {

        return ResponseEntity.ok(operatorService.updatePlan(planId, request));
    }

    // Delete plan
    @DeleteMapping("/plans/{planId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deletePlan(@PathVariable Long planId) {

        return ResponseEntity.ok(operatorService.deletePlan(planId));
    }
}