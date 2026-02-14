package com.ezycollect.server.application.mapper;

import com.ezycollect.server.application.dto.RegisterWebhookRequest;
import com.ezycollect.server.application.dto.WebhookResponse;
import com.ezycollect.server.domain.model.Webhook;

/**
 * Mapper for converting between Webhook domain models and DTOs.
 */
public class WebhookMapper {

    private WebhookMapper() {
        // Private constructor to prevent instantiation
    }

    /**
     * Maps a RegisterWebhookRequest DTO to a Webhook domain model.
     *
     * @param request the request DTO
     * @return the Webhook domain model
     */
    public static Webhook toDomain(RegisterWebhookRequest request) {
        return new Webhook(request.getEndpointUrl());
    }

    /**
     * Maps a Webhook domain model to a WebhookResponse DTO.
     *
     * @param webhook the Webhook domain model
     * @return the WebhookResponse DTO
     */
    public static WebhookResponse toResponse(Webhook webhook) {
        return WebhookResponse.builder().id(webhook.getId()).endpointUrl(webhook.getEndpointUrl())
                .active(webhook.isActive()).createdAt(webhook.getCreatedAt())
                .updatedAt(webhook.getUpdatedAt()).build();
    }
}
