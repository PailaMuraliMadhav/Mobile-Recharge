package com.example.userservice.controller;

import com.example.userservice.dto.*;
import com.example.userservice.enums.Role;
import com.example.userservice.security.JwtUtil;
import com.example.userservice.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private JwtUtil jwtUtil;

    @Test
    void testRegisterEndpoint() throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setEmail("murali@gmail.com");
        request.setPassword("Password123");
        request.setName("Murali");
        request.setPhoneNumber("6300252614");
        request.setRole(Role.USER);

        when(userService.register(any())).thenReturn(new UserResponse());

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated()); // controller returns 201
    }

    @Test
    void testLoginEndpoint() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setEmail("murali@gmail.com");
        request.setPassword("Password123");

        when(userService.login(any())).thenReturn(new LoginResponse());

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk()); // controller returns 200
    }
}