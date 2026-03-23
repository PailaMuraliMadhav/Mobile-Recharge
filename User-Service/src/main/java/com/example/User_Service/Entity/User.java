package com.example.User_Service.Entity;

import com.example.User_Service.Enums.Role;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@AllArgsConstructor
@Data
@NoArgsConstructor
@Builder
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    private String name;
    @Column(unique = true)
    private  String email;
    private  String password;
    @Enumerated(EnumType.STRING)
    private Role  role;
    @Column(unique = true)
    private  String phoneNumber;
    private  Boolean isActive;
    @Column(updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;
    private  LocalDateTime updatedAt;
}
