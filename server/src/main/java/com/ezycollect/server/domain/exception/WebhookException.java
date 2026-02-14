package com.ezycollect.server.domain.exception;

/**
 * Base exception for webhook-related domain errors.
 */
public abstract class WebhookException extends RuntimeException {

    protected WebhookException(String message) {
        super(message);
    }

    protected WebhookException(String message, Throwable cause) {
        super(message, cause);
    }
}
