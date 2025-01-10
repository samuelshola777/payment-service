package com.paymentService.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import lombok.Data;
import java.util.UUID;
import java.time.LocalDateTime;
import java.math.BigDecimal;

@Data
@Entity
@Table(name = "payments")
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private UUID customerId;

    @Column(nullable = true)
    private String accountNumber;

    @Column(nullable = true) 
    private String routingNumber;

    @Column(nullable = true)
    private String accountHolderName;

    @Column(nullable = true)
    private BigDecimal amount;

    @Column(nullable = true)
    private String currency;

    @Column(nullable = true)
    private String transferStatus;

    @Column(nullable = true)
    private String description;

    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
}
