package com.ezycollect.server.domain.model;

import com.ezycollect.server.domain.exception.WebhookValidationException;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Webhook domain entity representing a registered webhook endpoint. This is a pure domain model
 * without any framework dependencies.
 */
@Getter
@Setter
public class Webhook {

    private UUID id;
    private String endpointUrl;
    private boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Webhook() {
        this.id = UUID.randomUUID();
        this.active = true;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public Webhook(String endpointUrl) {
        this();
        this.endpointUrl = endpointUrl;
    }

    // Validation methods
    public void validate() {
        if (endpointUrl == null || endpointUrl.isBlank()) {
            throw new WebhookValidationException("Endpoint URL is required");
        }
        if (!endpointUrl.startsWith("http://") && !endpointUrl.startsWith("https://")) {
            throw new WebhookValidationException(
                    "Endpoint URL must start with http:// or https://");
        }
    }

    public void activate() {
        this.active = true;
        this.updatedAt = LocalDateTime.now();
    }

    public void deactivate() {
        this.active = false;
        this.updatedAt = LocalDateTime.now();
    }
}
