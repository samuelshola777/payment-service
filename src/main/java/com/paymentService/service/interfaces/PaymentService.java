package com.paymentService.service.interfaces;
import com.paymentService.dto.request.MakePaymentRequest;
import com.paymentService.dto.response.MakePaymentResponse;
import java.util.UUID;
import java.util.List;
import com.paymentService.dto.request.BankTransferRequest;
import com.paymentService.dto.response.BankTransferResponse;

public interface PaymentService {
    /**
     * Processes a payment transaction based on the provided payment request
     * @param request The payment request containing payment details
     * @return MakePaymentResponse containing the result of the payment transaction
     */
    MakePaymentResponse makePayment(MakePaymentRequest request);

    /**
     * Retrieves all payments associated with a specific customer
     * @param customerId The unique identifier of the customer
     * @return List of payment responses containing the payment history for the customer
     */
    List<MakePaymentResponse> getPaymentsByCustomerId(UUID customerId);

    /**
     * Processes a bank transfer request based on the provided bank transfer request
     * @param request The bank transfer request containing bank transfer details
     * @return BankTransferResponse containing the result of the bank transfer transaction
     */
    BankTransferResponse processBankTransfer(BankTransferRequest request);
}
