package com.paymentService.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import com.paymentService.dto.request.MakePaymentRequest;
import com.paymentService.service.interfaces.PaymentService;
import com.paymentService.dto.response.MakePaymentResponse;
import lombok.RequiredArgsConstructor;
import java.util.UUID;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import com.paymentService.dto.request.BankTransferRequest;
import com.paymentService.dto.response.BankTransferResponse;

@RestController
@RequestMapping("/api/payment")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    /**
     * Processes a payment request from a customer
     * 
     * @param request The payment request containing payment details
     * @return ResponseEntity containing the payment response if successful,
     *         or an error status if the payment processing fails
     */
    @PostMapping("/make-payment")
    public ResponseEntity<MakePaymentResponse> makePayment(@RequestBody  MakePaymentRequest request) {
        try {
            MakePaymentResponse response = paymentService.makePayment(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Retrieves all payments associated with a specific customer
     * 
     * @param customerId The unique identifier of the customer
     * @return ResponseEntity containing a list of payment responses if payments exist,
     *         NOT_FOUND if no payments exist, or an error status if the retrieval fails
     */
    @GetMapping("/customer/{customerId}/payments")
    public ResponseEntity<List<MakePaymentResponse>> getPaymentsByCustomerId(@PathVariable UUID customerId) {
        try {
            List<MakePaymentResponse> payments = paymentService.getPaymentsByCustomerId(customerId);
            if (payments == null || payments.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(payments);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    /**
     * Processes a bank transfer request
     * 
     * @param request The bank transfer request containing transfer details
     * @return ResponseEntity containing the bank transfer response if successful,
     *         or an error status if the transfer processing fails
     */
    @PostMapping("/bank-transfer")
    public ResponseEntity<BankTransferResponse> processBankTransfer(@RequestBody BankTransferRequest request) {
        try {
            BankTransferResponse response = paymentService.processBankTransfer(request);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

}
