package com.paymentService.dto.request;

import lombok.Data;
import java.math.BigDecimal;
import java.util.UUID;

@Data
public class MakePaymentRequest {
    private UUID customerId;
    private BigDecimal amount;
    private String paymentMethod;
}

