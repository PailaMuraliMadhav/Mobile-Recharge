package com.example.OperatorService.Controller;


import com.example.OperatorService.Dto.OperatorResponse;
import com.example.OperatorService.Dto.PlanResponse;
import com.example.OperatorService.Service.OperatorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/operators")
@RequiredArgsConstructor
public class OperatorController {

    private final OperatorService operatorService;

    //  Get All Operators
    @GetMapping
    public ResponseEntity<List<OperatorResponse>> getAllOperators() {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(operatorService.getAllOperators());
    }

    // Get Operator By Id
    @GetMapping("/{id}")
    public ResponseEntity<OperatorResponse> getOperatorById(
            @PathVariable Long id) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(operatorService.getOperatorById(id));
    }

    // Get Plans By Operator
    @GetMapping("/{operatorId}/plans")
    public ResponseEntity<List<PlanResponse>> getPlansByOperator(
            @PathVariable Long operatorId) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(operatorService.getPlansByOperator(operatorId));
    }

}
