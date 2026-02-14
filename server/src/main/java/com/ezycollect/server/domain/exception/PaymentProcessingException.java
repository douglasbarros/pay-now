package com.ezycollect.server.domain.exception;

/**
 * Exception thrown when payment processing fails. This represents a failure during payment
 * processing (e.g., gateway error, fraud detection).
 */
public class PaymentProcessingException extends PaymentException {

    public PaymentProcessingException(String message) {
        super(message);
    }

    public PaymentProcessingException(String message, Throwable cause) {
        super(message, cause);
    }
}
