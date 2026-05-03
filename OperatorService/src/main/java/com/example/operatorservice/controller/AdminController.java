package com.example.operatorservice.controller;

import com.example.operatorservice.dto.*;
import com.example.operatorservice.service.OperatorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/*
 * AUTHOR: Paila Murali Madhav
 * CLASS: AdminController
 * DESCRIPTION:
 *   REST controller that exposes admin-only endpoints for managing telecom operators
 *   and their recharge plans, including create, update, and soft-delete operations.
 */
@RestController
@RequestMapping("/api/admin/operators")
@RequiredArgsConstructor
public class AdminController {

    private final OperatorService operatorService;

    /* ================================================================
     * METHOD: getAllOperators (Admin)
     * DESCRIPTION:
     *   Returns all active operators for admin management.
     * ================================================================ */
    @GetMapping
    public ResponseEntity<java.util.List<OperatorResponse>> getAllOperators() {
        return ResponseEntity.ok(operatorService.getAllOperators());
    }

    /* ================================================================
     * METHOD: addOperator
     * DESCRIPTION:
     *   Creates a new telecom operator from the provided request payload.
     *   Returns HTTP 201 with the created operator details.
     * ================================================================ */
    @PostMapping
    public ResponseEntity<OperatorResponse> addOperator(
            @Valid @RequestBody OperatorRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(operatorService.addOperator(request));
    }

    /* ================================================================
     * METHOD: updateOperator
     * DESCRIPTION:
     *   Updates the details of an existing operator identified by its ID.
     * ================================================================ */
    @PutMapping("/{id}")
    public ResponseEntity<OperatorResponse> updateOperator(
            @PathVariable Long id,
            @Valid @RequestBody OperatorRequest request) {
        return ResponseEntity.ok(operatorService.updateOperator(id, request));
    }

    /* ================================================================
     * METHOD: deleteOperator
     * DESCRIPTION:
     *   Soft-deletes an operator by its ID, marking it as inactive without removing the record.
     * ================================================================ */
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteOperator(@PathVariable Long id) {
        return ResponseEntity.ok(operatorService.deleteOperator(id));
    }

    /* ================================================================
     * METHOD: addPlan
     * DESCRIPTION:
     *   Adds a new recharge plan to the specified operator.
     *   Returns HTTP 201 with the created plan details.
     * ================================================================ */
    @PostMapping("/{operatorId}/plans")
    public ResponseEntity<PlanResponse> addPlan(
            @PathVariable Long operatorId,
            @Valid @RequestBody PlanRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(operatorService.addPlan(operatorId, request));
    }

    /* ================================================================
     * METHOD: updatePlan
     * DESCRIPTION:
     *   Updates the details of an existing recharge plan identified by its plan ID.
     * ================================================================ */
    @PatchMapping("/plans/{planId}")
    public ResponseEntity<PlanResponse> updatePlan(
            @PathVariable Long planId,
            @Valid @RequestBody PlanRequest request) {
        return ResponseEntity.ok(operatorService.updatePlan(planId, request));
    }

    /* ================================================================
     * METHOD: deletePlan
     * DESCRIPTION:
     *   Soft-deletes a recharge plan by its ID, marking it as inactive without removing the record.
     * ================================================================ */
    @DeleteMapping("/plans/{planId}")
    public ResponseEntity<String> deletePlan(@PathVariable Long planId) {
        return ResponseEntity.ok(operatorService.deletePlan(planId));
    }
}
