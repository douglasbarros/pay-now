package com.ezycollect.server.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO for webhook payload sent to registered endpoints.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentWebhookPayload {

    private UUID paymentId;
    private String firstName;
    private String lastName;
    private String zipCode;
    private BigDecimal amount;
    private String status;
    private LocalDateTime createdAt;
    private String eventType;
    private String errorMessage;
}
