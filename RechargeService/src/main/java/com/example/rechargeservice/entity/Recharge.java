package com.example.rechargeservice.entity;

import com.example.rechargeservice.enums.RechargeStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/*
 * AUTHOR: Paila Murali Madhav
 * CLASS: Recharge
 * DESCRIPTION:
 *   JPA entity representing a mobile recharge transaction record stored in the database.
 *   Captures all details including operator, plan, payment mode, status, and timestamps.
 */
@Entity
@Table(name = "recharges")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Recharge {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private Long operatorId;

    @Column(nullable = false)
    private Long planId;

    @Column(nullable = false, length = 10)
    private String mobileNumber;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private RechargeStatus status = RechargeStatus.PENDING;

    @Column(length = 100)
    private String transactionId;

    private String operatorName;
    private String planName;
    private String planDescription;
    private Integer validityDays;
    private String userEmail;
    private String paymentMode;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
