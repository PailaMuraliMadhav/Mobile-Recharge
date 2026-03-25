package com.example.OperatorService.Controller;

import com.example.OperatorService.Dto.OperatorRequest;
import com.example.OperatorService.Dto.OperatorResponse;
import com.example.OperatorService.Dto.PlanRequest;
import com.example.OperatorService.Dto.PlanResponse;
import com.example.OperatorService.Service.OperatorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/operators")
@RequiredArgsConstructor
public class AdminController {

    private final OperatorService operatorService;

    // add operator
    @PostMapping
    public ResponseEntity<OperatorResponse> addOperator(
            @RequestBody OperatorRequest request) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(operatorService.addOperator(request));
    }

    //  update operator
    @PutMapping("/{id}")
    public ResponseEntity<OperatorResponse> updateOperator(
            @PathVariable Long id,
            @RequestBody OperatorRequest request) {

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(operatorService.updateOperator(id, request));
    }

    //  Delete Operator
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteOperator(
            @PathVariable Long id) {

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(operatorService.deleteOperator(id));
    }

    // Add Plan
    @PostMapping("/{operatorId}/plans")
    public ResponseEntity<PlanResponse> addPlan(
            @PathVariable Long operatorId,
            @RequestBody PlanRequest request) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(operatorService.addPlan(operatorId, request));
    }

    // Update Plan
    @PutMapping("/plans/{planId}")
    public ResponseEntity<PlanResponse> updatePlan(
            @PathVariable Long planId,
            @RequestBody PlanRequest request) {

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(operatorService.updatePlan(planId, request));
    }

    // Delete Plan
    @DeleteMapping("/plans/{planId}")
    public ResponseEntity<String> deletePlan(
            @PathVariable Long planId) {

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(operatorService.deletePlan(planId));
    }
}