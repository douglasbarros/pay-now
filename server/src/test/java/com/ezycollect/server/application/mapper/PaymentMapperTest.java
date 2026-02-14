package com.ezycollect.server.application.mapper;

import com.ezycollect.server.application.dto.CreatePaymentRequest;
import com.ezycollect.server.application.dto.PaymentResponse;
import com.ezycollect.server.application.dto.PaymentWebhookPayload;
import com.ezycollect.server.domain.model.Payment;
import com.ezycollect.server.domain.model.PaymentStatus;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class PaymentMapperTest {

    @Test
    void shouldMapCreateRequestToDomain() {
        CreatePaymentRequest request = new CreatePaymentRequest();
        request.setFirstName("John");
        request.setLastName("Doe");
        request.setZipCode("12345");
        request.setCardNumber("4532015112830366");
        request.setAmount(new BigDecimal("100.00"));

        Payment payment = PaymentMapper.toDomain(request, "encrypted123");

        assertEquals("John", payment.getFirstName());
        assertEquals("Doe", payment.getLastName());
        assertEquals("12345", payment.getZipCode());
        assertEquals("encrypted123", payment.getEncryptedCardNumber());
        assertEquals(new BigDecimal("100.00"), payment.getAmount());
        assertNotNull(payment.getId());
    }

    @Test
    void shouldMapDomainToResponse() {
        Payment payment =
                new Payment("John", "Doe", "12345", "encrypted123", new BigDecimal("100.00"));
        payment.setId(UUID.randomUUID());
        payment.setStatus(PaymentStatus.PROCESSED);
        payment.setCreatedAt(LocalDateTime.now());

        PaymentResponse response = PaymentMapper.toResponse(payment, "************0366");

        assertEquals(payment.getId(), response.getId());
        assertEquals("John", response.getFirstName());
        assertEquals("Doe", response.getLastName());
        assertEquals("12345", response.getZipCode());
        assertEquals("************0366", response.getMaskedCardNumber());
        assertEquals(new BigDecimal("100.00"), response.getAmount());
        assertEquals("PROCESSED", response.getStatus());
        assertNotNull(response.getCreatedAt());
    }

    @Test
    void shouldMapDomainToWebhookPayload() {
        Payment payment =
                new Payment("John", "Doe", "12345", "encrypted123", new BigDecimal("100.00"));
        payment.setId(UUID.randomUUID());
        payment.setStatus(PaymentStatus.PROCESSED);
        payment.setCreatedAt(LocalDateTime.now());

        PaymentWebhookPayload payload = PaymentMapper.toWebhookPayload(payment);

        assertEquals(payment.getId(), payload.getPaymentId());
        assertEquals("John", payload.getFirstName());
        assertEquals("Doe", payload.getLastName());
        assertEquals("12345", payload.getZipCode());
        assertEquals(new BigDecimal("100.00"), payload.getAmount());
        assertEquals("PROCESSED", payload.getStatus());
        assertEquals("payment.created", payload.getEventType());
    }

    @Test
    void shouldMaskCardNumberCorrectly() {
        String masked = PaymentMapper.maskCardNumber("4532015112830366");

        assertEquals("************0366", masked);
    }

    @Test
    void shouldMaskShortCardNumber() {
        String masked = PaymentMapper.maskCardNumber("1234567890123");

        assertEquals("*********0123", masked);
    }

    @Test
    void shouldHandleNullCardNumber() {
        String masked = PaymentMapper.maskCardNumber(null);

        assertEquals("****", masked);
    }

    @Test
    void shouldHandleShortInvalidCardNumber() {
        String masked = PaymentMapper.maskCardNumber("123");

        assertEquals("****", masked);
    }
}
