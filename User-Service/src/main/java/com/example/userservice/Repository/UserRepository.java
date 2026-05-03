package com.example.userservice.repository;

import com.example.userservice.entity.User;
import com.example.userservice.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

/*
 * AUTHOR: Paila Murali Madhav
 * CLASS: UserRepository
 * DESCRIPTION:
 *   Spring Data JPA repository interface for User entity database operations.
 *   Provides custom query methods for user lookup, duplicate checks, and role-based filtering.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /*
     * ================================================================
     * METHOD: existsByEmail
     * DESCRIPTION:
     * Checks whether a user with the given email address already exists.
     * ================================================================
     */
    boolean existsByEmail(String email);

    /*
     * ================================================================
     * METHOD: existsByPhoneNumber
     * DESCRIPTION:
     * Checks whether a user with the given phone number already exists.
     * ================================================================
     */
    boolean existsByPhoneNumber(String phoneNumber);

    /*
     * ================================================================
     * METHOD: findByEmailAndIsActiveTrue
     * DESCRIPTION:
     * Retrieves an active user by their email address. Used for authentication.
     * ================================================================
     */
    Optional<User> findByEmailAndIsActiveTrue(String email);

    /*
     * ================================================================
     * METHOD: findByEmail
     * DESCRIPTION:
     * Retrieves a user by their email address regardless of active status.
     * ================================================================
     */
    Optional<User> findByEmail(String email);

    /*
     * ================================================================
     * METHOD: findByRole
     * DESCRIPTION:
     * Retrieves all users with the specified role.
     * ================================================================
     */
    List<User> findByRole(Role role);

    /*
     * ================================================================
     * METHOD: existsByPhoneNumberAndIdNot
     * DESCRIPTION:
     * Checks for a duplicate phone number while excluding the user with the given
     * ID.
     * Used during profile update operations.
     * ================================================================
     */
    boolean existsByPhoneNumberAndIdNot(String phoneNumber, Long id);

    /*
     * ================================================================
     * METHOD: findByIsActiveTrue
     * DESCRIPTION:
     * Retrieves all users that are currently marked as active.
     * ================================================================
     */
    Collection<Object> findByIsActiveTrue();
}
