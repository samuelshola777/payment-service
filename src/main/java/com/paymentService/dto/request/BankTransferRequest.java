package com.paymentService.dto.request;

import java.math.BigDecimal;
import lombok.Data;

@Data
public class BankTransferRequest {
    private String accountNumber;
    private String routingNumber;
    private String accountHolderName;
    private BigDecimal amount;
    private String currency;
    private String description;
}
