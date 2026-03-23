package com.example.User_Service.Dto;

import com.example.User_Service.Enums.Role;
import lombok.AllArgsConstructor;
import lombok.Data;


@Data
@AllArgsConstructor
public class LoginResponse {
    private String token;
    private String type = "Bearer";
    private Long userId;
    private String email;
    private Role role;
    private long expiresIn;
}
