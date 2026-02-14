package com.ezycollect.server.domain.exception;

import java.util.UUID;

/**
 * Exception thrown when a webhook is not found.
 */
public class WebhookNotFoundException extends WebhookException {

    public WebhookNotFoundException(UUID webhookId) {
        super("Webhook not found with id: " + webhookId);
    }
}
