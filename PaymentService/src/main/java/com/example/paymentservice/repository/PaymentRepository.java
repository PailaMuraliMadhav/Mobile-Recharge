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

    Optional<Payment> findByTransactionId(String transactionId);

    List<Payment> findByUserId(Long id);

    Optional<Payment> findByRazorpayOrderId(String razorpayOrderId);
}
