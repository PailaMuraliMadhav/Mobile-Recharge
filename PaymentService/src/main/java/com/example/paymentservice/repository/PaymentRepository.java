package com.example.paymentservice.repository;

import com.example.paymentservice.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/*
 * AUTHOR: Paila Murali Madhav
 * CLASS: PaymentRepository
 * DESCRIPTION:
 *   Spring Data JPA repository interface for Payment entity database operations.
 *   Provides custom query methods for transaction lookup and user payment history.
 */
@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    /* ================================================================
     * METHOD: findByTransactionId
     * DESCRIPTION:
     *   Retrieves a payment record by its unique transaction ID.
     * ================================================================ */
    Optional<Payment> findByTransactionId(String transactionId);

    /* ================================================================
     * METHOD: findByUserId
     * DESCRIPTION:
     *   Retrieves all payment records associated with the given user ID.
     * ================================================================ */
    List<Payment> findByUserId(Long id);
}
