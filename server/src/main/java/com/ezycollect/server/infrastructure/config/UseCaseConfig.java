package com.ezycollect.server.infrastructure.config;

import com.ezycollect.server.application.usecase.PaymentUseCase;
import com.ezycollect.server.application.usecase.WebhookUseCase;
import com.ezycollect.server.domain.repository.PaymentRepository;
import com.ezycollect.server.domain.repository.WebhookRepository;
import com.ezycollect.server.domain.service.EncryptionService;
import com.ezycollect.server.domain.service.PaymentGateway;
import com.ezycollect.server.domain.service.WebhookNotificationService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class for use case beans. This wires the application layer with infrastructure
 * implementations.
 */
@Configuration
public class UseCaseConfig {

    @Bean
    public PaymentUseCase paymentUseCase(PaymentRepository paymentRepository,
            EncryptionService encryptionService,
            WebhookNotificationService webhookNotificationService, PaymentGateway paymentGateway) {
        return new PaymentUseCase(paymentRepository, encryptionService, webhookNotificationService,
                paymentGateway);
    }

    @Bean
    public WebhookUseCase webhookUseCase(WebhookRepository webhookRepository) {
        return new WebhookUseCase(webhookRepository);
    }
}
