package com.example.userservice.controller;

import com.example.userservice.dto.UpdateProfile;
import com.example.userservice.dto.UserResponse;
import com.example.userservice.security.JwtUtil;
import com.example.userservice.service.UserService;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private JwtUtil jwtUtil;   // ⭐ THIS FIXES YOUR ERROR


    // GET PROFILE
    @Test
    @WithMockUser(username = "murali@gmail.com")
    void getProfile_success() throws Exception {

        when(userService.getProfile("murali@gmail.com"))
                .thenReturn(new UserResponse());

        mockMvc.perform(get("/api/users/profile"))
                .andExpect(status().isOk());
    }

    // UPDATE PROFILE
    @Test
    @WithMockUser(username = "murali@gmail.com")
    void updateProfile_success() throws Exception {

        UpdateProfile req = new UpdateProfile();
        req.setName("Murali Updated");

        when(userService.updateProfile(
                org.mockito.ArgumentMatchers.eq("murali@gmail.com"),
                org.mockito.ArgumentMatchers.any()))
                .thenReturn(new UserResponse());

        mockMvc.perform(patch("/api/users/profile")
                        .with(csrf())
                        .contentType("application/json")
                        .content("""
                        {
                          "name":"Murali Updated"
                        }
                        """))
                .andExpect(status().isOk());
    }

    // DELETE ACCOUNT
    @Test
    @WithMockUser(username = "murali@gmail.com")
    void deleteAccount_success() throws Exception {

        when(userService.deleteMyAccount("murali@gmail.com"))
                .thenReturn("Your account has been permanently deleted");

        mockMvc.perform(delete("/api/users/profile")
                        .with(csrf()))
                .andExpect(status().isOk());
    }

    // RECHARGE HISTORY
    @Test
    @WithMockUser(username = "murali@gmail.com")
    void rechargeHistory_success() throws Exception {

        when(userService.getRechargeHistoryByEmail("murali@gmail.com"))
                .thenReturn(java.util.List.of());

        mockMvc.perform(get("/api/users/recharge-history"))
                .andExpect(status().isOk());
    }

    // TRANSACTION STATUS
    @Test
    @WithMockUser
    void transactionStatus_success() throws Exception {

        when(userService.getTransactionStatus("tx123"))
                .thenReturn(java.util.Map.of("status", "SUCCESS"));

        mockMvc.perform(get("/api/users/transaction/{transactionId}", "tx123")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}