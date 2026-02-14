package com.ezycollect.server.domain.model;

import com.ezycollect.server.domain.exception.WebhookValidationException;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class WebhookTest {

    @Test
    void shouldCreateWebhookWithValidUrl() {
        Webhook webhook = new Webhook("https://example.com/webhook");

        assertNotNull(webhook.getId());
        assertEquals("https://example.com/webhook", webhook.getEndpointUrl());
        assertTrue(webhook.isActive());
        assertNotNull(webhook.getCreatedAt());
        assertNotNull(webhook.getUpdatedAt());
    }

    @Test
    void shouldValidateWebhookSuccessfully() {
        Webhook webhook = new Webhook("https://example.com/webhook");

        assertDoesNotThrow(() -> webhook.validate());
    }

    @Test
    void shouldValidateWebhookWithHttpUrl() {
        Webhook webhook = new Webhook("http://example.com/webhook");

        assertDoesNotThrow(() -> webhook.validate());
    }

    @Test
    void shouldThrowExceptionWhenEndpointUrlIsNull() {
        Webhook webhook = new Webhook(null);

        WebhookValidationException exception =
                assertThrows(WebhookValidationException.class, () -> webhook.validate());

        assertEquals("Endpoint URL is required", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenEndpointUrlIsBlank() {
        Webhook webhook = new Webhook("  ");

        WebhookValidationException exception =
                assertThrows(WebhookValidationException.class, () -> webhook.validate());

        assertEquals("Endpoint URL is required", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenEndpointUrlDoesNotStartWithHttp() {
        Webhook webhook = new Webhook("ftp://example.com/webhook");

        WebhookValidationException exception =
                assertThrows(WebhookValidationException.class, () -> webhook.validate());

        assertEquals("Endpoint URL must start with http:// or https://", exception.getMessage());
    }

    @Test
    void shouldActivateWebhook() {
        Webhook webhook = new Webhook("https://example.com/webhook");
        webhook.setActive(false);
        LocalDateTime beforeUpdate = webhook.getUpdatedAt();

        // Small delay to ensure timestamp changes
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
        }

        webhook.activate();

        assertTrue(webhook.isActive());
        assertTrue(webhook.getUpdatedAt().isAfter(beforeUpdate)
                || webhook.getUpdatedAt().isEqual(beforeUpdate));
    }

    @Test
    void shouldDeactivateWebhook() {
        Webhook webhook = new Webhook("https://example.com/webhook");
        LocalDateTime beforeUpdate = webhook.getUpdatedAt();

        // Small delay to ensure timestamp changes
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
        }

        webhook.deactivate();

        assertFalse(webhook.isActive());
        assertTrue(webhook.getUpdatedAt().isAfter(beforeUpdate)
                || webhook.getUpdatedAt().isEqual(beforeUpdate));
    }
}
