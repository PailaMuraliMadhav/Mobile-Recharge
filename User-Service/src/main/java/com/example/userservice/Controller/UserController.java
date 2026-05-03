package com.example.userservice.controller;

import com.example.userservice.dto.UpdateProfile;
import com.example.userservice.dto.UserResponse;
import com.example.userservice.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/*
 * AUTHOR: Paila Murali Madhav
 * CLASS: UserController
 * DESCRIPTION:
 *   REST controller that exposes endpoints for authenticated users to manage their own
 *   profile, view recharge history, check transaction status, and delete their account.
 */
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {


    private  final UserService userService;

    /* ================================================================
     * METHOD: getProfile
     * DESCRIPTION:
     *   Returns the profile of the currently authenticated user extracted from the JWT token.
     * ================================================================ */
    @GetMapping("/profile")
    public ResponseEntity<UserResponse> getProfile(
            Authentication authentication) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(userService.getProfile(authentication.getName()));
    }

    /* ================================================================
     * METHOD: updateProfile
     * DESCRIPTION:
     *   Updates the name and/or phone number of the currently authenticated user's profile.
     * ================================================================ */
    @PatchMapping("/profile")
    public ResponseEntity<UserResponse> updateProfile(
            Authentication authentication,
            @RequestBody UpdateProfile request) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(userService.updateProfile(authentication.getName(), request));
    }

    /* ================================================================
     * METHOD: deleteMyAccount
     * DESCRIPTION:
     *   Permanently deletes the account of the currently authenticated user.
     * ================================================================ */
    @DeleteMapping("/profile")
    public ResponseEntity<String> deleteMyAccount(
            Authentication authentication) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(userService.deleteMyAccount(authentication.getName()));
    }

    /* ================================================================
     * METHOD: getRechargeHistory
     * DESCRIPTION:
     *   Fetches the recharge history for the authenticated user via a Feign call
     *   to the Recharge Service.
     * ================================================================ */
    @GetMapping("/recharge-history")
    public ResponseEntity<List<?>> getRechargeHistory(
            Authentication authentication) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(userService.getRechargeHistoryByEmail(authentication.getName()));
    }

    /* ================================================================
     * METHOD: getTransactionStatus
     * DESCRIPTION:
     *   Retrieves the payment transaction status for the given transaction ID
     *   via a Feign call to the Payment Service.
     * ================================================================ */
    @GetMapping("/transaction/{transactionId}")
    public ResponseEntity<Object> getTransactionStatus(
            @PathVariable String transactionId) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(userService.getTransactionStatus(transactionId));
    }

    /* ================================================================
     * METHOD: getInternalUserById
     * DESCRIPTION:
     *   Internal endpoint used by other microservices to retrieve user details by ID
     *   without going through the public API.
     * ================================================================ */
    @GetMapping("/internal/{id}")
    public ResponseEntity<UserResponse> getInternalUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }
}
