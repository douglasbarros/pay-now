package com.ezycollect.server.infrastructure.persistence.adapter;

import com.ezycollect.server.domain.model.Webhook;
import com.ezycollect.server.domain.repository.WebhookRepository;
import com.ezycollect.server.infrastructure.persistence.entity.WebhookEntity;
import com.ezycollect.server.infrastructure.persistence.jpa.JpaWebhookRepository;
import com.ezycollect.server.infrastructure.persistence.mapper.WebhookEntityMapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Adapter implementing the WebhookRepository port using JPA.
 */
@Component
public class WebhookRepositoryAdapter implements WebhookRepository {

    private final JpaWebhookRepository jpaWebhookRepository;

    public WebhookRepositoryAdapter(JpaWebhookRepository jpaWebhookRepository) {
        this.jpaWebhookRepository = jpaWebhookRepository;
    }

    @Override
    public Webhook save(Webhook webhook) {
        WebhookEntity entity = WebhookEntityMapper.toEntity(webhook);
        WebhookEntity savedEntity = jpaWebhookRepository.save(entity);
        return WebhookEntityMapper.toDomain(savedEntity);
    }

    @Override
    public Optional<Webhook> findById(UUID id) {
        return jpaWebhookRepository.findById(id)
                .map(WebhookEntityMapper::toDomain);
    }

    @Override
    public List<Webhook> findAllActive() {
        return jpaWebhookRepository.findByActiveTrue().stream()
                .map(WebhookEntityMapper::toDomain)
                .toList();
    }

    @Override
    public List<Webhook> findAll() {
        return jpaWebhookRepository.findAll().stream()
                .map(WebhookEntityMapper::toDomain)
                .toList();
    }

    @Override
    public void deleteById(UUID id) {
        jpaWebhookRepository.deleteById(id);
    }
}
