package com.ezycollect.server.infrastructure.persistence.jpa;

import com.ezycollect.server.infrastructure.persistence.entity.WebhookEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * Spring Data JPA repository for WebhookEntity.
 */
@Repository
public interface JpaWebhookRepository extends JpaRepository<WebhookEntity, UUID> {

    /**
     * Finds all active webhooks.
     *
     * @return list of active webhook entities
     */
    List<WebhookEntity> findByActiveTrue();
}
