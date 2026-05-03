package com.example.operatorservice.repository;

import com.example.operatorservice.entity.Plan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/*
 * AUTHOR: Paila Murali Madhav
 * CLASS: PlanRepository
 * DESCRIPTION:
 *   Spring Data JPA repository interface for Plan entity database operations.
 *   Provides custom query methods for plan lookup, active plan filtering, and duplicate checks.
 */
@Repository
public interface PlanRepository extends JpaRepository<Plan, Long> {

    /* ================================================================
     * METHOD: findByOperatorId
     * DESCRIPTION:
     *   Retrieves all plans (active and inactive) associated with the given operator ID.
     * ================================================================ */
    List<Plan> findByOperatorId(Long operatorId);

    /* ================================================================
     * METHOD: findByOperatorIdAndIsActiveTrue
     * DESCRIPTION:
     *   Retrieves only active plans associated with the given operator ID.
     * ================================================================ */
    List<Plan> findByOperatorIdAndIsActiveTrue(Long operatorId);

    /* ================================================================
     * METHOD: existsByNameAndOperatorId
     * DESCRIPTION:
     *   Checks whether a plan with the given name already exists for the specified operator.
     * ================================================================ */
    boolean existsByNameAndOperatorId(String name, Long operatorId);

    /* ================================================================
     * METHOD: existsByNameAndOperatorIdAndIdNot
     * DESCRIPTION:
     *   Checks for a duplicate plan name within an operator while excluding the plan with the given ID.
     *   Used during update operations.
     * ================================================================ */
    boolean existsByNameAndOperatorIdAndIdNot(String name, Long operatorId, Long id);
}
