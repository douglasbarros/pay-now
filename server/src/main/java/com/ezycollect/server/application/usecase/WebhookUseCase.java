package com.ezycollect.server.application.usecase;

import com.ezycollect.server.application.dto.RegisterWebhookRequest;
import com.ezycollect.server.application.dto.WebhookResponse;
import com.ezycollect.server.application.mapper.WebhookMapper;
import com.ezycollect.server.domain.exception.WebhookNotFoundException;
import com.ezycollect.server.domain.model.Webhook;
import com.ezycollect.server.domain.repository.WebhookRepository;

import java.util.List;
import java.util.UUID;

/**
 * Use case for managing webhooks. This orchestrates webhook registration and management logic.
 */
public class WebhookUseCase {

    private final WebhookRepository webhookRepository;

    public WebhookUseCase(WebhookRepository webhookRepository) {
        this.webhookRepository = webhookRepository;
    }

    /**
     * Registers a new webhook endpoint.
     *
     * @param request the webhook registration request
     * @return the registered webhook response
     */
    public WebhookResponse registerWebhook(RegisterWebhookRequest request) {
        Webhook webhook = WebhookMapper.toDomain(request);
        webhook.validate();

        Webhook savedWebhook = webhookRepository.save(webhook);
        return WebhookMapper.toResponse(savedWebhook);
    }

    /**
     * Retrieves a webhook by its ID.
     *
     * @param id the webhook ID
     * @return the webhook response
     * @throws WebhookNotFoundException if webhook is not found
     */
    public WebhookResponse getWebhookById(UUID id) {
        Webhook webhook =
                webhookRepository.findById(id).orElseThrow(() -> new WebhookNotFoundException(id));

        return WebhookMapper.toResponse(webhook);
    }

    /**
     * Retrieves all webhooks.
     *
     * @return list of webhook responses
     */
    public List<WebhookResponse> getAllWebhooks() {
        return webhookRepository.findAll().stream().map(WebhookMapper::toResponse).toList();
    }

    /**
     * Deletes a webhook by its ID.
     *
     * @param id the webhook ID
     * @throws WebhookNotFoundException if webhook is not found
     */
    public void deleteWebhook(UUID id) {
        if (!webhookRepository.findById(id).isPresent()) {
            throw new WebhookNotFoundException(id);
        }
        webhookRepository.deleteById(id);
    }

    /**
     * Activates a webhook.
     *
     * @param id the webhook ID
     * @return the updated webhook response
     * @throws WebhookNotFoundException if webhook is not found
     */
    public WebhookResponse activateWebhook(UUID id) {
        Webhook webhook =
                webhookRepository.findById(id).orElseThrow(() -> new WebhookNotFoundException(id));

        webhook.activate();
        Webhook updatedWebhook = webhookRepository.save(webhook);
        return WebhookMapper.toResponse(updatedWebhook);
    }

    /**
     * Deactivates a webhook.
     *
     * @param id the webhook ID
     * @return the updated webhook response
     * @throws WebhookNotFoundException if webhook is not found
     */
    public WebhookResponse deactivateWebhook(UUID id) {
        Webhook webhook =
                webhookRepository.findById(id).orElseThrow(() -> new WebhookNotFoundException(id));

        webhook.deactivate();
        Webhook updatedWebhook = webhookRepository.save(webhook);
        return WebhookMapper.toResponse(updatedWebhook);
    }
}
