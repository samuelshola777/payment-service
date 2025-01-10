package com.paymentService.model.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.paymentService.model.Payment;
import java.util.UUID;

public interface PaymentRepository extends JpaRepository<Payment, UUID> {
    Payment findByCustomerId(UUID customerId);
}
