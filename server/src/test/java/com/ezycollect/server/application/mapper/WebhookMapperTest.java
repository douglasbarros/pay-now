package com.ezycollect.server.application.mapper;

import com.ezycollect.server.application.dto.RegisterWebhookRequest;
import com.ezycollect.server.application.dto.WebhookResponse;
import com.ezycollect.server.domain.model.Webhook;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class WebhookMapperTest {

    @Test
    void shouldMapRegisterRequestToDomain() {
        RegisterWebhookRequest request = new RegisterWebhookRequest();
        request.setEndpointUrl("https://example.com/webhook");

        Webhook webhook = WebhookMapper.toDomain(request);

        assertEquals("https://example.com/webhook", webhook.getEndpointUrl());
        assertTrue(webhook.isActive());
        assertNotNull(webhook.getId());
    }

    @Test
    void shouldMapDomainToResponse() {
        Webhook webhook = new Webhook("https://example.com/webhook");
        webhook.setId(UUID.randomUUID());
        webhook.setCreatedAt(LocalDateTime.now());
        webhook.setUpdatedAt(LocalDateTime.now());

        WebhookResponse response = WebhookMapper.toResponse(webhook);

        assertEquals(webhook.getId(), response.getId());
        assertEquals("https://example.com/webhook", response.getEndpointUrl());
        assertTrue(response.isActive());
        assertNotNull(response.getCreatedAt());
        assertNotNull(response.getUpdatedAt());
    }
}
