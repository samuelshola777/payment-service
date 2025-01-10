package com.paymentService.model.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.paymentService.model.Customer;
import java.util.UUID;
public interface CustomerRepository extends JpaRepository<Customer, UUID> {
    Customer findByCustomerId(UUID customerId);
}
