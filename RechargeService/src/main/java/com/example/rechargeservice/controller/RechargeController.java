package com.example.rechargeservice.controller;

import com.example.rechargeservice.dto.RechargeRequest;
import com.example.rechargeservice.dto.RechargeResponse;
import com.example.rechargeservice.service.RechargeService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/recharges")
@RequiredArgsConstructor
public class RechargeController {

    private final RechargeService rechargeService;

    @PostMapping
    public ResponseEntity<RechargeResponse> recharge(
            @Valid @RequestBody RechargeRequest request) {

        RechargeResponse response =
                rechargeService.processRecharge(request);

        return ResponseEntity.ok(response);
    }
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<RechargeResponse>> getRechargeHistory(
            @PathVariable Long userId) {

        return ResponseEntity.ok(
                rechargeService.getRechargeHistory(userId)
        );
    }
    @GetMapping
    public ResponseEntity<List<RechargeResponse>> getAllRecharges() {

        return ResponseEntity.ok(
                rechargeService.getAllRecharges()
        );
    }
}