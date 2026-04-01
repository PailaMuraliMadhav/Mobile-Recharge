package com.example.rechargeservice.repository;


import com.example.rechargeservice.entity.Recharge;
import com.example.rechargeservice.enums.RechargeStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RechargeRepository extends JpaRepository<Recharge, Long> {

    List<Recharge> findByUserId(Long userId);
    boolean existsByUserIdAndMobileNumberAndPlanIdAndStatus(
            Long userId,
            String mobileNumber,
            Long planId,
            RechargeStatus status
    );
}
