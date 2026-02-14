package com.ezycollect.server.application.mapper;

import com.ezycollect.server.application.dto.CreatePaymentRequest;
import com.ezycollect.server.application.dto.PaymentResponse;
import com.ezycollect.server.application.dto.PaymentWebhookPayload;
import com.ezycollect.server.domain.model.Payment;

/**
 * Mapper for converting between Payment domain models and DTOs.
 */
public class PaymentMapper {

    private PaymentMapper() {
        // Private constructor to prevent instantiation
    }

    /**
     * Maps a CreatePaymentRequest DTO to a Payment domain model.
     *
     * @param request the request DTO
     * @param encryptedCardNumber the encrypted card number
     * @return the Payment domain model
     */
    public static Payment toDomain(CreatePaymentRequest request, String encryptedCardNumber) {
        return new Payment(request.getFirstName(), request.getLastName(), request.getZipCode(),
                encryptedCardNumber, request.getAmount());
    }

    /**
     * Maps a Payment domain model to a PaymentResponse DTO.
     *
     * @param payment the Payment domain model
     * @param maskedCardNumber the masked card number for display
     * @return the PaymentResponse DTO
     */
    public static PaymentResponse toResponse(Payment payment, String maskedCardNumber) {
        return PaymentResponse.builder().id(payment.getId()).firstName(payment.getFirstName())
                .lastName(payment.getLastName()).zipCode(payment.getZipCode())
                .maskedCardNumber(maskedCardNumber).amount(payment.getAmount())
                .status(payment.getStatus().name()).createdAt(payment.getCreatedAt()).build();
    }

    /**
     * Maps a Payment domain model to a webhook payload.
     *
     * @param payment the Payment domain model
     * @return the PaymentWebhookPayload DTO
     */
    public static PaymentWebhookPayload toWebhookPayload(Payment payment) {
        return PaymentWebhookPayload.builder().paymentId(payment.getId())
                .firstName(payment.getFirstName()).lastName(payment.getLastName())
                .zipCode(payment.getZipCode()).amount(payment.getAmount())
                .status(payment.getStatus().name()).createdAt(payment.getCreatedAt())
                .eventType("payment.created").build();
    }

    /**
     * Masks a card number for display purposes.
     *
     * @param cardNumber the original card number
     * @return masked card number (e.g., ************1234)
     */
    public static String maskCardNumber(String cardNumber) {
        if (cardNumber == null || cardNumber.length() < 4) {
            return "****";
        }
        int length = cardNumber.length();
        String lastFour = cardNumber.substring(length - 4);
        return "*".repeat(length - 4) + lastFour;
    }
}
