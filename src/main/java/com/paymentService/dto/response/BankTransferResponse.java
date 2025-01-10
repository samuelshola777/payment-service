package com.paymentService.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Data;
import lombok.AllArgsConstructor;

@Data
@AllArgsConstructor
public class BankTransferResponse {
    private String transferId;
    private String accountNumber;
    private String routingNumber; 
    private String accountHolderName;
    private BigDecimal amount;
    private String currency;
    private String status;
    private LocalDateTime transferDate;
    private String description;
}
