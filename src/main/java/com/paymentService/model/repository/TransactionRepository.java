package com.paymentService.model.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.paymentService.model.Transaction;
import java.util.UUID;
import java.util.List;


public interface TransactionRepository extends JpaRepository<Transaction, UUID> {
    List<Transaction> findAllByPaymentId(UUID paymentId);
}
