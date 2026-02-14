package com.ezycollect.server.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO for webhook response.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WebhookResponse {

    private UUID id;
    private String endpointUrl;
    private boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
