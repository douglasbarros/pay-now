package com.ezycollect.server.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO for payment response.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentResponse {

    private UUID id;
    private String firstName;
    private String lastName;
    private String zipCode;
    private String maskedCardNumber;
    private BigDecimal amount;
    private String status;
    private LocalDateTime createdAt;
}
