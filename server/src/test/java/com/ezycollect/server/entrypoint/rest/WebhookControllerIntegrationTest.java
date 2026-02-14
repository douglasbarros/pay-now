package com.ezycollect.server.entrypoint.rest;

import com.ezycollect.server.application.dto.RegisterWebhookRequest;
import com.ezycollect.server.domain.model.Webhook;
import com.ezycollect.server.domain.repository.WebhookRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class WebhookControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private WebhookRepository webhookRepository;

    @Test
    void shouldRegisterWebhookSuccessfully() throws Exception {
        RegisterWebhookRequest request = new RegisterWebhookRequest();
        request.setEndpointUrl("https://webhook.site/unique-url");

        mockMvc.perform(post("/api/webhooks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.endpointUrl").value("https://webhook.site/unique-url"))
                .andExpect(jsonPath("$.active").value(true));
    }

    @Test
    void shouldReturnBadRequestWhenEndpointUrlIsMissing() throws Exception {
        RegisterWebhookRequest request = new RegisterWebhookRequest();

        mockMvc.perform(post("/api/webhooks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void shouldReturnBadRequestWhenEndpointUrlIsInvalid() throws Exception {
        RegisterWebhookRequest request = new RegisterWebhookRequest();
        request.setEndpointUrl("ftp://invalid-url");

        mockMvc.perform(post("/api/webhooks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void shouldGetWebhookByIdSuccessfully() throws Exception {
        Webhook webhook = new Webhook("https://webhook.site/test");
        Webhook saved = webhookRepository.save(webhook);

        mockMvc.perform(get("/api/webhooks/" + saved.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(saved.getId().toString()))
                .andExpect(jsonPath("$.endpointUrl").value("https://webhook.site/test"))
                .andExpect(jsonPath("$.active").value(true));
    }

    @Test
    void shouldReturnNotFoundWhenWebhookDoesNotExist() throws Exception {
        UUID nonExistentId = UUID.randomUUID();

        mockMvc.perform(get("/api/webhooks/" + nonExistentId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(containsString("Webhook not found")));
    }

    @Test
    void shouldGetAllWebhooksSuccessfully() throws Exception {
        Webhook webhook1 = new Webhook("https://webhook.site/test1");
        Webhook webhook2 = new Webhook("https://webhook.site/test2");
        webhookRepository.save(webhook1);
        webhookRepository.save(webhook2);

        mockMvc.perform(get("/api/webhooks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(2))))
                .andExpect(jsonPath("$[*].endpointUrl", hasItem("https://webhook.site/test1")))
                .andExpect(jsonPath("$[*].endpointUrl", hasItem("https://webhook.site/test2")));
    }

    @Test
    void shouldDeleteWebhookSuccessfully() throws Exception {
        Webhook webhook = new Webhook("https://webhook.site/to-delete");
        Webhook saved = webhookRepository.save(webhook);

        mockMvc.perform(delete("/api/webhooks/" + saved.getId()))
                .andExpect(status().isNoContent());
    }

    @Test
    void shouldActivateWebhookSuccessfully() throws Exception {
        Webhook webhook = new Webhook("https://webhook.site/to-activate");
        webhook.setActive(false);
        Webhook saved = webhookRepository.save(webhook);

        mockMvc.perform(patch("/api/webhooks/" + saved.getId() + "/activate"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.active").value(true));
    }

    @Test
    void shouldDeactivateWebhookSuccessfully() throws Exception {
        Webhook webhook = new Webhook("https://webhook.site/to-deactivate");
        Webhook saved = webhookRepository.save(webhook);

        mockMvc.perform(patch("/api/webhooks/" + saved.getId() + "/deactivate"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.active").value(false));
    }
}
