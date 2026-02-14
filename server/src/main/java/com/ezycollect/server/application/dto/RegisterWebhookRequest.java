package com.ezycollect.server.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for registering a new webhook.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterWebhookRequest {

    @NotBlank(message = "Endpoint URL is required")
    @Pattern(regexp = "^https?://.*", message = "Endpoint URL must start with http:// or https://")
    private String endpointUrl;
}
