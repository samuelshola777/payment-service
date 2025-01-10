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
}
