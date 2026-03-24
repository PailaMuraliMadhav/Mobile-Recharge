package com.example.User_Service.Controller;

import com.example.User_Service.Dto.*;
import com.example.User_Service.Service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;
    //  Own Profile
    @GetMapping("/profile")
    public ResponseEntity<UserResponse> getProfile(
            Authentication authentication) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(userService.getProfile(authentication.getName()));
    }

    // Update  Profile
    @PatchMapping("/profile")
    public ResponseEntity<UserResponse> updateProfile(
            Authentication authentication,
            @RequestBody UpdateProfile request) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(userService.updateProfile(authentication.getName(), request));
    }

    //  Delete Account
    @DeleteMapping("/profile")
    public ResponseEntity<String> deleteMyAccount(
            Authentication authentication) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(userService.deleteMyAccount(authentication.getName()));
    }

    // Recharge History
    @GetMapping("/recharge-history")
    public ResponseEntity<List<?>> getRechargeHistory(
            Authentication authentication) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(userService.getRechargeHistoryByEmail(authentication.getName()));
    }

    // Transaction Status
    @GetMapping("/transaction/{transactionId}")
    public ResponseEntity<Object> getTransactionStatus(
            @PathVariable Long transactionId) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(userService.getTransactionStatus(transactionId));
    }
}