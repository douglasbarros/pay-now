package com.ezycollect.server.domain.exception;

import java.util.UUID;

/**
 * Exception thrown when a payment is not found. This represents a resource not found scenario.
 */
public class PaymentNotFoundException extends PaymentException {

    public PaymentNotFoundException(UUID paymentId) {
        super("Payment not found with id: " + paymentId);
    }
}
