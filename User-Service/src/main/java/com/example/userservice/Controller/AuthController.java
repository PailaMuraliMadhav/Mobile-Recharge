package com.example.userservice.controller;

import com.example.userservice.dto.*;
import com.example.userservice.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/*
 * AUTHOR: Paila Murali Madhav
 * CLASS: AuthController
 * DESCRIPTION:
 *   REST controller that handles user authentication endpoints including
 *   OTP generation, verification, user registration, and login with JWT token generation.
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    private static final String KEY_MESSAGE = "message";

    /* ================================================================
     * METHOD: register
     * DESCRIPTION:
     *   Registers a new user account with the provided details.
     *   Returns HTTP 201 with the created user profile on success.
     * ================================================================ */
    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@RequestBody @Valid RegisterRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.register(request));
    }

    /* ================================================================
     * METHOD: login
     * DESCRIPTION:
     *   Authenticates a user with their email and password, returning a JWT token
     *   and user details on successful authentication.
     * ================================================================ */
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody @Valid LoginRequest request) {
        return ResponseEntity.ok(userService.login(request));
    }
}
