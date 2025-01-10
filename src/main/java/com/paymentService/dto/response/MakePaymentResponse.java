package com.paymentService.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
public class MakePaymentResponse {
    private UUID transactionId;
    private BigDecimal amount;
    private String status;
    private LocalDateTime createdAt;
}
