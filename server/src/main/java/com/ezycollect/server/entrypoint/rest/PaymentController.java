package com.ezycollect.server.entrypoint.rest;

import com.ezycollect.server.application.dto.CreatePaymentRequest;
import com.ezycollect.server.application.dto.PageResponse;
import com.ezycollect.server.application.dto.PaymentResponse;
import com.ezycollect.server.application.usecase.PaymentUseCase;
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
 * REST controller for payment operations.
 */
@RestController
@RequestMapping("/api/payments")
@Tag(name = "Payments", description = "Payment management APIs")
public class PaymentController {

    private final PaymentUseCase paymentUseCase;

    public PaymentController(PaymentUseCase paymentUseCase) {
        this.paymentUseCase = paymentUseCase;
    }

    @PostMapping
    @Operation(summary = "Create a new payment", description = "Creates a new payment with encrypted card information")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Payment created successfully", content = @Content(schema = @Schema(implementation = PaymentResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "422", description = "Validation error", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<PaymentResponse> createPayment(@Valid @RequestBody CreatePaymentRequest request) {
        PaymentResponse response = paymentUseCase.createPayment(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get payment by ID", description = "Retrieves a payment by its unique identifier")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Payment found", content = @Content(schema = @Schema(implementation = PaymentResponse.class))),
            @ApiResponse(responseCode = "404", description = "Payment not found", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<PaymentResponse> getPaymentById(@PathVariable UUID id) {
        PaymentResponse response = paymentUseCase.getPaymentById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @Operation(summary = "Get all payments", description = "Retrieves all payments with optional pagination")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of payments", content = @Content(schema = @Schema(implementation = PaymentResponse.class)))
    })
    public ResponseEntity<?> getAllPayments(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size) {

        // If pagination parameters are provided, return paginated response
        if (page != null && size != null) {
            PageResponse<PaymentResponse> pageResponse = paymentUseCase.getPaymentsPaginated(page, size);
            return ResponseEntity.ok(pageResponse);
        }

        // Otherwise, return all payments (backward compatibility)
        List<PaymentResponse> responses = paymentUseCase.getAllPayments();
        return ResponseEntity.ok(responses);
    }
}
