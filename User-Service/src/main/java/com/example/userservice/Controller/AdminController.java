package com.example.userservice.controller;

import com.example.userservice.dto.UserResponse;
import com.example.userservice.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/*
 * AUTHOR: Paila Murali Madhav
 * CLASS: AdminController
 * DESCRIPTION:
 *   REST controller that exposes admin-only endpoints for managing user accounts,
 *   including listing all users, retrieving a user by ID, and blocking/unblocking users.
 *   Admins cannot be created via the API; they must be inserted directly via SQL.
 */
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final UserService userService;

    /* ================================================================
     * METHOD: getAllUsers
     * DESCRIPTION:
     *   Returns a list of all registered users. Restricted to users with the ADMIN role.
     * ================================================================ */
    @GetMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    /* ================================================================
     * METHOD: getUserById
     * DESCRIPTION:
     *   Retrieves a single user by their ID. Restricted to users with the ADMIN role.
     * ================================================================ */
    @GetMapping("/users/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    /* ================================================================
     * METHOD: blockUser
     * DESCRIPTION:
     *   Blocks a user account by setting their active flag to false, preventing login.
     *   Restricted to users with the ADMIN role.
     * ================================================================ */
    @PatchMapping("/users/{id}/block")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> blockUser(@PathVariable Long id) {
        return ResponseEntity.ok(userService.blockUser(id));
    }

    /* ================================================================
     * METHOD: unblockUser
     * DESCRIPTION:
     *   Unblocks a previously blocked user account, restoring their ability to log in.
     *   Restricted to users with the ADMIN role.
     * ================================================================ */
    @PatchMapping("/users/{id}/unblock")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> unblockUser(@PathVariable Long id) {
        return ResponseEntity.ok(userService.unblockUser(id));
    }
}
