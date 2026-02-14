package com.ezycollect.server.domain.exception;

/**
 * Exception thrown when webhook validation fails.
 */
public class WebhookValidationException extends WebhookException {

    public WebhookValidationException(String message) {
        super(message);
    }
}
