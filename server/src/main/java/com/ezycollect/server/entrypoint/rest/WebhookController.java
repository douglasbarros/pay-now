package com.ezycollect.server.entrypoint.rest;

import com.ezycollect.server.application.dto.RegisterWebhookRequest;
import com.ezycollect.server.application.dto.WebhookResponse;
import com.ezycollect.server.application.usecase.WebhookUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * REST controller for webhook operations.
 */
@RestController
@RequestMapping("/api/webhooks")
@Tag(name = "Webhooks", description = "Webhook management APIs")
public class WebhookController {

    private final WebhookUseCase webhookUseCase;

    public WebhookController(WebhookUseCase webhookUseCase) {
        this.webhookUseCase = webhookUseCase;
    }

    @PostMapping
    @Operation(summary = "Register a new webhook", description = "Registers a webhook endpoint to receive payment notifications")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Webhook registered successfully", content = @Content(schema = @Schema(implementation = WebhookResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "422", description = "Validation error", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<WebhookResponse> registerWebhook(@Valid @RequestBody RegisterWebhookRequest request) {
        WebhookResponse response = webhookUseCase.registerWebhook(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get webhook by ID", description = "Retrieves a webhook by its unique identifier")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Webhook found", content = @Content(schema = @Schema(implementation = WebhookResponse.class))),
            @ApiResponse(responseCode = "404", description = "Webhook not found", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<WebhookResponse> getWebhookById(@PathVariable UUID id) {
        WebhookResponse response = webhookUseCase.getWebhookById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @Operation(summary = "Get all webhooks", description = "Retrieves all registered webhooks")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of webhooks", content = @Content(schema = @Schema(implementation = WebhookResponse.class)))
    })
    public ResponseEntity<List<WebhookResponse>> getAllWebhooks() {
        List<WebhookResponse> responses = webhookUseCase.getAllWebhooks();
        return ResponseEntity.ok(responses);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete webhook", description = "Deletes a webhook by its unique identifier")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Webhook deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Webhook not found", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<Void> deleteWebhook(@PathVariable UUID id) {
        webhookUseCase.deleteWebhook(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/activate")
    @Operation(summary = "Activate webhook", description = "Activates a webhook")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Webhook activated", content = @Content(schema = @Schema(implementation = WebhookResponse.class))),
            @ApiResponse(responseCode = "404", description = "Webhook not found", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<WebhookResponse> activateWebhook(@PathVariable UUID id) {
        WebhookResponse response = webhookUseCase.activateWebhook(id);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/deactivate")
    @Operation(summary = "Deactivate webhook", description = "Deactivates a webhook")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Webhook deactivated", content = @Content(schema = @Schema(implementation = WebhookResponse.class))),
            @ApiResponse(responseCode = "404", description = "Webhook not found", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<WebhookResponse> deactivateWebhook(@PathVariable UUID id) {
        WebhookResponse response = webhookUseCase.deactivateWebhook(id);
        return ResponseEntity.ok(response);
    }
}
