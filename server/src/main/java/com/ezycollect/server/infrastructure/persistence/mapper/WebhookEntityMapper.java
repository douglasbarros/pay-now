package com.ezycollect.server.infrastructure.persistence.mapper;

import com.ezycollect.server.domain.model.Webhook;
import com.ezycollect.server.infrastructure.persistence.entity.WebhookEntity;

/**
 * Mapper between Webhook domain model and WebhookEntity.
 */
public class WebhookEntityMapper {

    private WebhookEntityMapper() {
        // Private constructor to prevent instantiation
    }

    public static WebhookEntity toEntity(Webhook webhook) {
        return WebhookEntity.builder().id(webhook.getId()).endpointUrl(webhook.getEndpointUrl())
                .active(webhook.isActive()).createdAt(webhook.getCreatedAt())
                .updatedAt(webhook.getUpdatedAt()).build();
    }

    public static Webhook toDomain(WebhookEntity entity) {
        Webhook webhook = new Webhook();
        webhook.setId(entity.getId());
        webhook.setEndpointUrl(entity.getEndpointUrl());
        webhook.setActive(entity.isActive());
        webhook.setCreatedAt(entity.getCreatedAt());
        webhook.setUpdatedAt(entity.getUpdatedAt());
        return webhook;
    }
}
