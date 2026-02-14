package com.ezycollect.server.application.usecase;

import com.ezycollect.server.application.dto.CreatePaymentRequest;
import com.ezycollect.server.application.dto.PageResponse;
import com.ezycollect.server.application.dto.PaymentResponse;
import com.ezycollect.server.application.mapper.PaymentMapper;
import com.ezycollect.server.domain.exception.PaymentNotFoundException;
import com.ezycollect.server.domain.exception.PaymentProcessingException;
import com.ezycollect.server.domain.model.Payment;
import com.ezycollect.server.domain.model.PaymentGatewayResponse;
import com.ezycollect.server.domain.repository.PaymentRepository;
import com.ezycollect.server.domain.service.EncryptionService;
import com.ezycollect.server.domain.service.PaymentGateway;
import com.ezycollect.server.domain.service.WebhookNotificationService;

import java.util.List;
import java.util.UUID;

/**
 * Use case for creating and managing payments. This orchestrates the domain logic and coordinates
 * between different services.
 */
public class PaymentUseCase {

    private final PaymentRepository paymentRepository;
    private final EncryptionService encryptionService;
    private final WebhookNotificationService webhookNotificationService;
    private final PaymentGateway paymentGateway;

    public PaymentUseCase(PaymentRepository paymentRepository, EncryptionService encryptionService,
            WebhookNotificationService webhookNotificationService, PaymentGateway paymentGateway) {
        this.paymentRepository = paymentRepository;
        this.encryptionService = encryptionService;
        this.webhookNotificationService = webhookNotificationService;
        this.paymentGateway = paymentGateway;
    }

    /**
     * Creates a new payment and triggers webhook notifications.
     *
     * @param request the payment creation request
     * @return the created payment response
     */
    public PaymentResponse createPayment(CreatePaymentRequest request) {
        // Encrypt card number before storing
        String encryptedCardNumber = encryptionService.encrypt(request.getCardNumber());

        // Create domain model
        Payment payment = PaymentMapper.toDomain(request, encryptedCardNumber);
        payment.validate();

        // Save payment in PENDING status
        Payment savedPayment = paymentRepository.save(payment);

        try {
            // Mark payment as PROCESSING before starting
            savedPayment.markAsProcessing();
            savedPayment = paymentRepository.save(savedPayment);

            // Process the payment (integrate with payment gateway, fraud detection, etc.)
            processPayment(savedPayment);

            // Mark as PROCESSED after successful processing
            savedPayment.markAsProcessed();
            savedPayment = paymentRepository.save(savedPayment);

            // Notify webhooks about successful payment
            webhookNotificationService.notifyPaymentCreated(savedPayment);

        } catch (Exception e) {
            // Mark payment as FAILED if any error occurs during processing
            savedPayment.markAsFailed();
            savedPayment = paymentRepository.save(savedPayment);

            // Notify webhooks about the failure
            String errorMessage = "Payment processing failed: " + e.getMessage();
            webhookNotificationService.notifyPaymentFailed(savedPayment, errorMessage);

            // Re-throw as domain exception
            throw new PaymentProcessingException(errorMessage, e);
        }

        // Return response with masked card number
        String maskedCardNumber = PaymentMapper.maskCardNumber(request.getCardNumber());
        return PaymentMapper.toResponse(savedPayment, maskedCardNumber);
    }

    /**
     * Processes a payment through the payment gateway.
     *
     * @param payment the payment to process
     * @throws RuntimeException if processing fails
     */
    private void processPayment(Payment payment) {
        // Fraud detection logic
        if ("aaa".equals(payment.getFirstName()) && "aaa".equals(payment.getLastName())) {
            throw new RuntimeException("Fraud detected");
        }

        // Call payment gateway
        PaymentGatewayResponse response = paymentGateway.process(payment);
        if (!response.isSuccess()) {
            throw new RuntimeException("Payment gateway error: " + response.getErrorMessage());
        }
    }

    /**
     * Retrieves a payment by its ID.
     *
     * @param id the payment ID
     * @return the payment response
     * @throws PaymentNotFoundException if payment is not found
     */
    public PaymentResponse getPaymentById(UUID id) {
        Payment payment =
                paymentRepository.findById(id).orElseThrow(() -> new PaymentNotFoundException(id));

        // Decrypt card number to mask it for display
        String decryptedCardNumber = encryptionService.decrypt(payment.getEncryptedCardNumber());
        String maskedCardNumber = PaymentMapper.maskCardNumber(decryptedCardNumber);

        return PaymentMapper.toResponse(payment, maskedCardNumber);
    }

    /**
     * Retrieves all payments.
     *
     * @return list of payment responses
     */
    public List<PaymentResponse> getAllPayments() {
        return paymentRepository.findAll().stream().map(payment -> {
            String decryptedCardNumber =
                    encryptionService.decrypt(payment.getEncryptedCardNumber());
            String maskedCardNumber = PaymentMapper.maskCardNumber(decryptedCardNumber);
            return PaymentMapper.toResponse(payment, maskedCardNumber);
        }).toList();
    }

    /**
     * Retrieves a page of payments.
     *
     * @param page the page number (0-indexed)
     * @param size the page size
     * @return paginated payment responses
     */
    public PageResponse<PaymentResponse> getPaymentsPaginated(int page, int size) {
        List<Payment> payments = paymentRepository.findAll(page, size);
        long totalElements = paymentRepository.count();

        List<PaymentResponse> paymentResponses = payments.stream().map(payment -> {
            String decryptedCardNumber =
                    encryptionService.decrypt(payment.getEncryptedCardNumber());
            String maskedCardNumber = PaymentMapper.maskCardNumber(decryptedCardNumber);
            return PaymentMapper.toResponse(payment, maskedCardNumber);
        }).toList();

        return new PageResponse<>(paymentResponses, page, size, totalElements);
    }
}
