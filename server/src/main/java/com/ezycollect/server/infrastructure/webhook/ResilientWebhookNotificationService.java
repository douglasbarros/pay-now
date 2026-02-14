package com.ezycollect.server.infrastructure.webhook;

import com.ezycollect.server.application.dto.PaymentWebhookPayload;
import com.ezycollect.server.application.mapper.PaymentMapper;
import com.ezycollect.server.domain.model.Payment;
import com.ezycollect.server.domain.model.Webhook;
import com.ezycollect.server.domain.repository.WebhookRepository;
import com.ezycollect.server.domain.service.WebhookNotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;

/**
 * Implementation of WebhookNotificationService with resilience features. Supports async execution
 * and retry mechanism for failed webhook calls.
 */
@Service
public class ResilientWebhookNotificationService implements WebhookNotificationService {

    private static final Logger logger =
            LoggerFactory.getLogger(ResilientWebhookNotificationService.class);

    private final WebhookRepository webhookRepository;
    private final WebClient webClient;

    public ResilientWebhookNotificationService(WebhookRepository webhookRepository,
            WebClient.Builder webClientBuilder) {
        this.webhookRepository = webhookRepository;
        this.webClient = webClientBuilder.defaultHeader("Content-Type", "application/json").build();
    }

    @Override
    @Async
    public void notifyPaymentCreated(Payment payment) {
        List<Webhook> activeWebhooks = webhookRepository.findAllActive();

        if (activeWebhooks.isEmpty()) {
            logger.info("No active webhooks to notify for payment: {}", payment.getId());
            return;
        }

        PaymentWebhookPayload payload = PaymentMapper.toWebhookPayload(payment);

        for (Webhook webhook : activeWebhooks) {
            try {
                sendWebhookNotification(webhook.getEndpointUrl(), payload);
            } catch (Exception e) {
                logger.error("Failed to notify webhook {} for payment {}: {}",
                        webhook.getEndpointUrl(), payment.getId(), e.getMessage());
            }
        }
    }

    @Override
    @Async
    public void notifyPaymentFailed(Payment payment, String errorMessage) {
        List<Webhook> activeWebhooks = webhookRepository.findAllActive();

        if (activeWebhooks.isEmpty()) {
            logger.info("No active webhooks to notify for failed payment: {}", payment.getId());
            return;
        }

        PaymentWebhookPayload payload = PaymentMapper.toWebhookPayload(payment);
        payload.setErrorMessage(errorMessage);

        for (Webhook webhook : activeWebhooks) {
            try {
                sendWebhookNotification(webhook.getEndpointUrl(), payload);
                logger.info("Notified webhook {} about payment failure: {}",
                        webhook.getEndpointUrl(), payment.getId());
            } catch (Exception e) {
                logger.error("Failed to notify webhook {} about payment failure {}: {}",
                        webhook.getEndpointUrl(), payment.getId(), e.getMessage());
            }
        }
    }

    /**
     * Sends webhook notification with retry capability. Retries up to 3 times with exponential
     * backoff (2s, 4s, 8s).
     */
    @Retryable(maxAttempts = 3, backoff = @Backoff(delay = 2000, multiplier = 2))
    private void sendWebhookNotification(String webhookUrl, PaymentWebhookPayload payload) {
        logger.info("Sending webhook notification to: {}", webhookUrl);

        try {
            webClient.post().uri(webhookUrl).bodyValue(payload).retrieve().bodyToMono(String.class)
                    .timeout(Duration.ofSeconds(10)).onErrorResume(e -> {
                        logger.error("Webhook call failed for URL {}: {}", webhookUrl,
                                e.getMessage());
                        return Mono.error(e);
                    }).block();

            logger.info("Webhook notification sent successfully to: {}", webhookUrl);
        } catch (Exception e) {
            logger.error("Error sending webhook to {}: {}", webhookUrl, e.getMessage());
            throw new RuntimeException("Webhook notification failed", e);
        }
    }
}
