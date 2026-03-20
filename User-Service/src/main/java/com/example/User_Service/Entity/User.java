package com.example.User_Service.Entity;

import com.example.User_Service.Enums.Role;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@AllArgsConstructor
@Data
@NoArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    private String name;

    private  String email;
    private  String password;
    private Role  role;
    private  String phoneNumber;
    private  Boolean isActive;
    private LocalDateTime createdAt;
    private  LocalDateTime updatedAt;
}
