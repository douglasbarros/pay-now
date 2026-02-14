package com.ezycollect.server.application.usecase;

import com.ezycollect.server.application.dto.RegisterWebhookRequest;
import com.ezycollect.server.application.dto.WebhookResponse;
import com.ezycollect.server.domain.exception.WebhookNotFoundException;
import com.ezycollect.server.domain.exception.WebhookValidationException;
import com.ezycollect.server.domain.model.Webhook;
import com.ezycollect.server.domain.repository.WebhookRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WebhookUseCaseTest {

    @Mock
    private WebhookRepository webhookRepository;

    private WebhookUseCase webhookUseCase;

    @BeforeEach
    void setUp() {
        webhookUseCase = new WebhookUseCase(webhookRepository);
    }

    @Test
    void shouldRegisterWebhookSuccessfully() {
        RegisterWebhookRequest request = new RegisterWebhookRequest();
        request.setEndpointUrl("https://example.com/webhook");

        Webhook savedWebhook = new Webhook("https://example.com/webhook");
        savedWebhook.setId(UUID.randomUUID());

        when(webhookRepository.save(any(Webhook.class))).thenReturn(savedWebhook);

        WebhookResponse response = webhookUseCase.registerWebhook(request);

        assertNotNull(response);
        assertEquals("https://example.com/webhook", response.getEndpointUrl());
        assertTrue(response.isActive());

        verify(webhookRepository).save(any(Webhook.class));
    }

    @Test
    void shouldThrowExceptionWhenWebhookValidationFails() {
        RegisterWebhookRequest request = new RegisterWebhookRequest();
        request.setEndpointUrl("ftp://invalid-url");

        assertThrows(WebhookValidationException.class,
                () -> webhookUseCase.registerWebhook(request));

        verify(webhookRepository, never()).save(any(Webhook.class));
    }

    @Test
    void shouldGetWebhookByIdSuccessfully() {
        UUID webhookId = UUID.randomUUID();
        Webhook webhook = new Webhook("https://example.com/webhook");
        webhook.setId(webhookId);

        when(webhookRepository.findById(webhookId)).thenReturn(Optional.of(webhook));

        WebhookResponse response = webhookUseCase.getWebhookById(webhookId);

        assertNotNull(response);
        assertEquals(webhookId, response.getId());
        assertEquals("https://example.com/webhook", response.getEndpointUrl());

        verify(webhookRepository).findById(webhookId);
    }

    @Test
    void shouldThrowExceptionWhenWebhookNotFound() {
        UUID webhookId = UUID.randomUUID();

        when(webhookRepository.findById(webhookId)).thenReturn(Optional.empty());

        WebhookNotFoundException exception = assertThrows(WebhookNotFoundException.class,
                () -> webhookUseCase.getWebhookById(webhookId));

        assertTrue(exception.getMessage().contains("Webhook not found"));
        verify(webhookRepository).findById(webhookId);
    }

    @Test
    void shouldGetAllWebhooksSuccessfully() {
        Webhook webhook1 = new Webhook("https://example.com/webhook1");
        webhook1.setId(UUID.randomUUID());
        Webhook webhook2 = new Webhook("https://example.com/webhook2");
        webhook2.setId(UUID.randomUUID());

        when(webhookRepository.findAll()).thenReturn(List.of(webhook1, webhook2));

        List<WebhookResponse> responses = webhookUseCase.getAllWebhooks();

        assertEquals(2, responses.size());
        assertEquals("https://example.com/webhook1", responses.get(0).getEndpointUrl());
        assertEquals("https://example.com/webhook2", responses.get(1).getEndpointUrl());

        verify(webhookRepository).findAll();
    }

    @Test
    void shouldDeleteWebhookSuccessfully() {
        UUID webhookId = UUID.randomUUID();
        Webhook webhook = new Webhook("https://example.com/webhook");
        webhook.setId(webhookId);

        when(webhookRepository.findById(webhookId)).thenReturn(Optional.of(webhook));

        assertDoesNotThrow(() -> webhookUseCase.deleteWebhook(webhookId));

        verify(webhookRepository).findById(webhookId);
        verify(webhookRepository).deleteById(webhookId);
    }

    @Test
    void shouldThrowExceptionWhenDeletingNonExistentWebhook() {
        UUID webhookId = UUID.randomUUID();

        when(webhookRepository.findById(webhookId)).thenReturn(Optional.empty());

        WebhookNotFoundException exception = assertThrows(WebhookNotFoundException.class,
                () -> webhookUseCase.deleteWebhook(webhookId));

        assertTrue(exception.getMessage().contains("Webhook not found"));
        verify(webhookRepository).findById(webhookId);
        verify(webhookRepository, never()).deleteById(any());
    }

    @Test
    void shouldActivateWebhookSuccessfully() {
        UUID webhookId = UUID.randomUUID();
        Webhook webhook = new Webhook("https://example.com/webhook");
        webhook.setId(webhookId);
        webhook.setActive(false);

        when(webhookRepository.findById(webhookId)).thenReturn(Optional.of(webhook));
        when(webhookRepository.save(any(Webhook.class))).thenReturn(webhook);

        WebhookResponse response = webhookUseCase.activateWebhook(webhookId);

        assertNotNull(response);
        assertTrue(response.isActive());

        verify(webhookRepository).findById(webhookId);
        verify(webhookRepository).save(webhook);
    }

    @Test
    void shouldDeactivateWebhookSuccessfully() {
        UUID webhookId = UUID.randomUUID();
        Webhook webhook = new Webhook("https://example.com/webhook");
        webhook.setId(webhookId);

        when(webhookRepository.findById(webhookId)).thenReturn(Optional.of(webhook));
        when(webhookRepository.save(any(Webhook.class))).thenReturn(webhook);

        WebhookResponse response = webhookUseCase.deactivateWebhook(webhookId);

        assertNotNull(response);
        assertFalse(response.isActive());

        verify(webhookRepository).findById(webhookId);
        verify(webhookRepository).save(webhook);
    }
}
