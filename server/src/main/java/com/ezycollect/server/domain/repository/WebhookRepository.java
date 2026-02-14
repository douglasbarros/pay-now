package com.ezycollect.server.domain.repository;

import com.ezycollect.server.domain.model.Webhook;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Port (interface) for webhook persistence operations.
 * This defines the contract that infrastructure adapters must implement.
 */
public interface WebhookRepository {

    /**
     * Saves a webhook to the persistence layer.
     *
     * @param webhook the webhook to save
     * @return the saved webhook
     */
    Webhook save(Webhook webhook);

    /**
     * Finds a webhook by its unique identifier.
     *
     * @param id the webhook ID
     * @return an Optional containing the webhook if found
     */
    Optional<Webhook> findById(UUID id);

    /**
     * Retrieves all active webhooks.
     *
     * @return list of active webhooks
     */
    List<Webhook> findAllActive();

    /**
     * Retrieves all webhooks.
     *
     * @return list of all webhooks
     */
    List<Webhook> findAll();

    /**
     * Deletes a webhook by its ID.
     *
     * @param id the webhook ID
     */
    void deleteById(UUID id);
}
