package com.ezycollect.server.domain.exception;

/**
 * Base exception for payment-related domain errors. This is a domain exception that represents
 * business rule violations.
 */
public abstract class PaymentException extends RuntimeException {

    protected PaymentException(String message) {
        super(message);
    }

    protected PaymentException(String message, Throwable cause) {
        super(message, cause);
    }
}
