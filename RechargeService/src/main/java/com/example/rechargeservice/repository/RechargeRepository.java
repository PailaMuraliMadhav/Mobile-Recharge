package com.example.rechargeservice.repository;

import com.example.rechargeservice.entity.Recharge;
import com.example.rechargeservice.enums.RechargeStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/*
 * AUTHOR: Paila Murali Madhav
 * CLASS: RechargeRepository
 * DESCRIPTION:
 *   Spring Data JPA repository interface for Recharge entity database operations.
 *   Provides custom query methods for fetching recharge history and checking duplicate active recharges.
 */
public interface RechargeRepository extends JpaRepository<Recharge, Long> {

    /* ================================================================
     * METHOD: findByUserId
     * DESCRIPTION:
     *   Retrieves all recharge records associated with the given user ID.
     * ================================================================ */
    List<Recharge> findByUserId(Long userId);

    /* ================================================================
     * METHOD: existsByUserIdAndMobileNumberAndPlanIdAndStatus
     * DESCRIPTION:
     *   Checks whether a user already has an active recharge for the given mobile number and plan.
     *   Used to prevent duplicate recharges.
     * ================================================================ */
    boolean existsByUserIdAndMobileNumberAndPlanIdAndStatus(
            Long userId,
            String mobileNumber,
            Long planId,
            RechargeStatus status
    );

    List<Recharge> findByStatus(RechargeStatus status);
}
