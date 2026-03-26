package com.example.RechargeService.Repository;


import com.example.RechargeService.Entity.Recharge;
import com.example.RechargeService.Enums.RechargeStatus;
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
