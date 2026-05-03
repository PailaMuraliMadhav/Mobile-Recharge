package com.example.userservice.controller;

import com.example.userservice.dto.UserResponse;
import com.example.userservice.security.JwtUtil;
import com.example.userservice.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AdminController.class)
@AutoConfigureMockMvc(addFilters = false)
class AdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private JwtUtil jwtUtil;

    @Test
    void testGetAllUsersEndpoint() throws Exception {
        when(userService.getAllUsers()).thenReturn(List.of(new UserResponse()));

        mockMvc.perform(get("/api/admin/users"))
                .andExpect(status().isOk());
    }

    @Test
    void testGetUserByIdEndpoint() throws Exception {
        when(userService.getUserById(anyLong())).thenReturn(new UserResponse());

        mockMvc.perform(get("/api/admin/users/1"))
                .andExpect(status().isOk());
    }

    @Test
    void testBlockUserEndpoint() throws Exception {
        when(userService.blockUser(anyLong())).thenReturn("User blocked successfully");

        mockMvc.perform(patch("/api/admin/users/1/block"))
                .andExpect(status().isOk());
    }

    @Test
    void testUnblockUserEndpoint() throws Exception {
        when(userService.unblockUser(anyLong())).thenReturn("User unblocked successfully");

        mockMvc.perform(patch("/api/admin/users/1/unblock"))
                .andExpect(status().isOk());
    }
}
