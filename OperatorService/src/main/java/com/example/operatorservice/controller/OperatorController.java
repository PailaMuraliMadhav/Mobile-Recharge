package com.example.operatorservice.controller;

import com.example.operatorservice.dto.OperatorResponse;
import com.example.operatorservice.dto.PlanResponse;
import com.example.operatorservice.service.OperatorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/*
 * AUTHOR: Paila Murali Madhav
 * CLASS: OperatorController
 * DESCRIPTION:
 *   REST controller that exposes public read-only endpoints for retrieving
 *   operators and their associated recharge plans.
 */
@RestController
@RequestMapping("/api/operators")
@RequiredArgsConstructor
public class OperatorController {

    private final OperatorService operatorService;

    /* ================================================================
     * METHOD: getAllOperators
     * DESCRIPTION:
     *   Returns a list of all currently active telecom operators.
     * ================================================================ */
    @GetMapping
    public ResponseEntity<List<OperatorResponse>> getAllOperators() {
        return ResponseEntity.ok(operatorService.getAllOperators());
    }

    /* ================================================================
     * METHOD: getOperatorById
     * DESCRIPTION:
     *   Retrieves a single operator by its unique ID.
     * ================================================================ */
    @GetMapping("/{id}")
    public ResponseEntity<OperatorResponse> getOperatorById(@PathVariable Long id) {
        return ResponseEntity.ok(operatorService.getOperatorById(id));
    }

    /* ================================================================
     * METHOD: getPlansByOperator
     * DESCRIPTION:
     *   Returns all active recharge plans available for the specified operator.
     * ================================================================ */
    @GetMapping("/{operatorId}/plans")
    public ResponseEntity<List<PlanResponse>> getPlansByOperator(
            @PathVariable Long operatorId) {
        return ResponseEntity.ok(operatorService.getPlansByOperator(operatorId));
    }

    /* ================================================================
     * METHOD: getPlanById
     * DESCRIPTION:
     *   Retrieves a single recharge plan by its unique plan ID.
     * ================================================================ */
    @GetMapping("/plans/{planId}")
    public ResponseEntity<PlanResponse> getPlanById(@PathVariable Long planId) {
        return ResponseEntity.ok(operatorService.getPlanById(planId));
    }
}
