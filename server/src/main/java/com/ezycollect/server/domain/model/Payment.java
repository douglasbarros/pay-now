package com.ezycollect.server.domain.model;

import com.ezycollect.server.domain.exception.PaymentValidationException;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Payment domain entity representing a payment transaction. This is a pure domain model without any
 * framework dependencies.
 */
@Getter
@Setter
public class Payment {

    private UUID id;
    private String firstName;
    private String lastName;
    private String zipCode;
    private String encryptedCardNumber;
    private BigDecimal amount;
    private LocalDateTime createdAt;
    private PaymentStatus status;

    public Payment() {
        this.id = UUID.randomUUID();
        this.createdAt = LocalDateTime.now();
        this.status = PaymentStatus.PENDING;
    }

    public Payment(String firstName, String lastName, String zipCode, String encryptedCardNumber,
            BigDecimal amount) {
        this();
        this.firstName = firstName;
        this.lastName = lastName;
        this.zipCode = zipCode;
        this.encryptedCardNumber = encryptedCardNumber;
        this.amount = amount;
    }

    // Validation methods
    public void validate() {
        if (firstName == null || firstName.isBlank()) {
            throw new PaymentValidationException("First name is required");
        }
        if (lastName == null || lastName.isBlank()) {
            throw new PaymentValidationException("Last name is required");
        }
        if (zipCode == null || zipCode.isBlank()) {
            throw new PaymentValidationException("Zip code is required");
        }
        if (encryptedCardNumber == null || encryptedCardNumber.isBlank()) {
            throw new PaymentValidationException("Card number is required");
        }
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new PaymentValidationException("Amount must be greater than 0");
        }
    }

    public void markAsProcessing() {
        this.status = PaymentStatus.PROCESSING;
    }

    public void markAsProcessed() {
        this.status = PaymentStatus.PROCESSED;
    }

    public void markAsFailed() {
        this.status = PaymentStatus.FAILED;
    }
}
