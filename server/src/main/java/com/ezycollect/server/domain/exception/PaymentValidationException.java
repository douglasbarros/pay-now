package com.ezycollect.server.domain.exception;

/**
 * Exception thrown when payment validation fails. This represents a business rule violation during
 * payment creation.
 */
public class PaymentValidationException extends PaymentException {

    public PaymentValidationException(String message) {
        super(message);
    }
}
