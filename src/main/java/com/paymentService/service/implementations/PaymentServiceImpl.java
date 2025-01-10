package com.paymentService.service.implementations;

import com.paymentService.service.interfaces.PaymentService;
import com.paymentService.dto.request.MakePaymentRequest;
import com.paymentService.dto.response.MakePaymentResponse;
import org.springframework.stereotype.Service;
import java.util.UUID;
import java.util.List;
import lombok.RequiredArgsConstructor;
import com.paymentService.model.repository.CustomerRepository;
import com.paymentService.model.repository.PaymentRepository;
import com.paymentService.model.repository.TransactionRepository;
import com.paymentService.model.Customer;
import com.paymentService.model.Payment;
import java.time.LocalDateTime;
import com.paymentService.model.Transaction;
import java.util.ArrayList;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import com.paymentService.dto.request.BankTransferRequest;
import com.paymentService.dto.response.BankTransferResponse;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {
    private final CustomerRepository customerRepository;
    private final PaymentRepository paymentRepository;
    private final TransactionRepository transactionRepository;

    /**
     * Processes a payment request for a customer.
     * If the customer doesn't exist, creates a new customer record along with associated payment and transaction.
     * If the customer exists, creates or updates payment record and creates a new transaction.
     *
     * @param request The payment request containing customer ID and payment amount
     * @return MakePaymentResponse containing transaction details (ID, amount, status, and timestamp)
     */
    @Override
    public MakePaymentResponse makePayment(MakePaymentRequest request) {
        Customer customer = customerRepository.findByCustomerId(request.getCustomerId());

        if (customer == null) {
           customer = new Customer();
           customer.setCustomerId(request.getCustomerId());
           customer.setCreatedAt(LocalDateTime.now());
           Customer savedCustomer = customerRepository.save(customer);
           Payment payment = new Payment();
           payment.setCustomerId(savedCustomer.getId());
           payment.setCreatedAt(LocalDateTime.now());
           Payment savedPayment = paymentRepository.save(payment);
           Transaction transaction = new Transaction();
           transaction.setPaymentId(savedPayment.getId());
           transaction.setAmount(request.getAmount());
           transaction.setStatus("pending");
           transaction.setCreatedAt(LocalDateTime.now());
           Transaction savedTransaction = transactionRepository.save(transaction);
           return new MakePaymentResponse(savedTransaction.getId(), savedTransaction.getAmount(), savedTransaction.getStatus(), savedTransaction.getCreatedAt());
        }
        Payment payment = paymentRepository.findByCustomerId(customer.getId());
        Payment savedPayment = null;
        if (payment == null) {
            payment = new Payment(); 
        }
         payment.setCustomerId(customer.getId());
        payment.setCreatedAt(LocalDateTime.now());
        savedPayment = paymentRepository.save(payment);
        Transaction transaction = new Transaction();
        transaction.setPaymentId(savedPayment.getId());
        transaction.setAmount(request.getAmount());
        transaction.setStatus("pending");
        transaction.setCreatedAt(LocalDateTime.now());
        Transaction savedTransaction = transactionRepository.save(transaction);
        return new MakePaymentResponse(savedTransaction.getId(), savedTransaction.getAmount(), savedTransaction.getStatus(), savedTransaction.getCreatedAt());
    }

    /**
     * Retrieves all payment transactions for a specific customer.
     * Looks up customer by customer ID, then finds associated payment and transactions.
     *
     * @param customerId The UUID of the customer whose payments are to be retrieved
     * @return List of MakePaymentResponse containing all transaction details for the customer,
     *         or null if customer, payment, or transactions are not found
     */
    @Override
    public List<MakePaymentResponse> getPaymentsByCustomerId(UUID customerId) {
       Customer inHouseCustomer = customerRepository.findByCustomerId(customerId);
       if (inHouseCustomer == null) {
        return null;
       }
       Payment payment = paymentRepository.findByCustomerId(inHouseCustomer.getId());
       if (payment == null) {
        return null;
       }
       List<Transaction> transactions = transactionRepository.findAllByPaymentId(payment.getId());
       if (transactions == null) {
        return null;
       }
       List<MakePaymentResponse> responses = new ArrayList<>();
       for (Transaction transaction : transactions) {
        responses.add(new MakePaymentResponse(transaction.getId(), transaction.getAmount(), transaction.getStatus(), transaction.getCreatedAt()));
       }
        return responses;
    }

    @Override
    public BankTransferResponse processBankTransfer(BankTransferRequest request) {
        // Input validation
        if (request == null || 
            request.getAccountNumber() == null || request.getAccountNumber().trim().isEmpty() ||
            request.getRoutingNumber() == null || request.getRoutingNumber().trim().isEmpty() ||
            request.getAccountHolderName() == null || request.getAccountHolderName().trim().isEmpty() ||
            request.getAmount() == null || request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Invalid bank transfer request parameters");
        }

        // Validate routing number format (assuming US routing numbers)
        if (!request.getRoutingNumber().matches("^\\d{9}$")) {
            throw new IllegalArgumentException("Invalid routing number format");
        }

        // Create payment record
        Payment payment = new Payment();
        payment.setAccountNumber(request.getAccountNumber());
        payment.setRoutingNumber(request.getRoutingNumber());
        payment.setAccountHolderName(request.getAccountHolderName());
        payment.setAmount(request.getAmount());
        payment.setCurrency(request.getCurrency());
        payment.setDescription(request.getDescription());
        payment.setTransferStatus("PENDING");
        payment.setCreatedAt(LocalDateTime.now());
        payment.setUpdatedAt(LocalDateTime.now());
        Payment savedPayment = paymentRepository.save(payment);

        // Create transaction record
        Transaction transaction = new Transaction();
        transaction.setTransactionId(UUID.randomUUID().toString());
        transaction.setPaymentId(savedPayment.getId());
        transaction.setAmount(request.getAmount());
        transaction.setCurrency(request.getCurrency());
        transaction.setAccountNumber(request.getAccountNumber());
        transaction.setRoutingNumber(request.getRoutingNumber());
        transaction.setAccountHolderName(request.getAccountHolderName());
        transaction.setStatus("PROCESSING");
        transaction.setDescription(request.getDescription());
        transaction.setCreatedAt(LocalDateTime.now());
        transaction.setUpdatedAt(LocalDateTime.now());
        Transaction savedTransaction = transactionRepository.save(transaction);

        try {
            // Mock external banking service call
            boolean transferSuccess = mockExternalBankingService(
                savedTransaction.getTransactionId(),
                request.getAccountNumber(),
                request.getRoutingNumber(),
                request.getAmount()
            );

            if (!transferSuccess) {
                throw new RuntimeException("External banking service rejected the transfer");
            }

            // Update transaction status
            savedTransaction.setStatus("COMPLETED");
            savedTransaction.setUpdatedAt(LocalDateTime.now());
            savedTransaction = transactionRepository.save(savedTransaction);

            // Update payment status
            savedPayment.setTransferStatus("COMPLETED");
            savedPayment.setUpdatedAt(LocalDateTime.now());
            paymentRepository.save(savedPayment);

            return new BankTransferResponse(
                savedTransaction.getTransactionId(),
                savedTransaction.getAccountNumber(),
                savedTransaction.getRoutingNumber(),
                savedTransaction.getAccountHolderName(),
                savedTransaction.getAmount(),
                savedTransaction.getCurrency(),
                savedTransaction.getStatus(),
                savedTransaction.getCreatedAt(),
                savedTransaction.getDescription()
            );

        } catch (Exception e) {
            // Handle failure
            savedTransaction.setStatus("FAILED");
            savedTransaction.setUpdatedAt(LocalDateTime.now());
            transactionRepository.save(savedTransaction);

            savedPayment.setTransferStatus("FAILED");
            savedPayment.setUpdatedAt(LocalDateTime.now());
            paymentRepository.save(savedPayment);

            throw new RuntimeException("Bank transfer processing failed: " + e.getMessage());
        }
    }

    /**
     * Mocks an external banking service call
     * In a real implementation, this would be replaced with actual external service integration
     */
    private boolean mockExternalBankingService(String transactionId, String accountNumber, 
                                             String routingNumber, BigDecimal amount) {
        // Create HTTP client
        RestTemplate restTemplate = new RestTemplate();
        String externalBankingUrl = "https://external-banking-api.example.com/transactions";

        // Prepare request body
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("transactionId", transactionId);
        requestBody.put("accountNumber", accountNumber);
        requestBody.put("routingNumber", routingNumber);
        requestBody.put("amount", amount);

        try {
            // Make HTTP POST request to external banking service
            ResponseEntity<Map> response = restTemplate.postForEntity(
                externalBankingUrl,
                requestBody,
                Map.class
            );

            // Check if request was successful (HTTP 200)
            if (response.getStatusCode() == HttpStatus.OK) {
                Map<String, Object> responseBody = response.getBody();
                return responseBody != null && "SUCCESS".equals(responseBody.get("status"));
            }
            return false;

        } catch (Exception e) {
            // Log error and return false if external service call fails
            return false;
        }
    }
}
