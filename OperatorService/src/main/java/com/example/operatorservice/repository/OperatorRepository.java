package com.example.operatorservice.repository;

import com.example.operatorservice.entity.Operator;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/*
 * AUTHOR: Paila Murali Madhav
 * CLASS: OperatorRepository
 * DESCRIPTION:
 *   Spring Data JPA repository interface for Operator entity database operations.
 *   Provides custom query methods for operator lookup, duplicate checks, and active operator retrieval.
 */
@Repository
public interface OperatorRepository extends JpaRepository<Operator, Long> {

    /* ================================================================
     * METHOD: existsByName
     * DESCRIPTION:
     *   Checks whether an operator with the given name already exists in the database.
     * ================================================================ */
    boolean existsByName(String name);

    /* ================================================================
     * METHOD: existsByNameAndIdNot
     * DESCRIPTION:
     *   Checks for a duplicate operator name while excluding the operator with the given ID.
     *   Used during update operations to allow keeping the same name.
     * ================================================================ */
    boolean existsByNameAndIdNot(String name, Long id);

    /* ================================================================
     * METHOD: findByIsActiveTrue
     * DESCRIPTION:
     *   Retrieves all operators that are currently marked as active.
     * ================================================================ */
    List<Operator> findByIsActiveTrue();
}
