package com.ezycollect.server.domain.model;

import com.ezycollect.server.domain.exception.PaymentValidationException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class PaymentTest {

    @Test
    void shouldCreatePaymentWithValidData() {
        Payment payment =
                new Payment("John", "Doe", "12345", "encrypted123", new BigDecimal("100.00"));

        assertNotNull(payment.getId());
        assertEquals("John", payment.getFirstName());
        assertEquals("Doe", payment.getLastName());
        assertEquals("12345", payment.getZipCode());
        assertEquals("encrypted123", payment.getEncryptedCardNumber());
        assertEquals(new BigDecimal("100.00"), payment.getAmount());
        assertEquals(PaymentStatus.PENDING, payment.getStatus());
        assertNotNull(payment.getCreatedAt());
    }

    @Test
    void shouldValidatePaymentSuccessfully() {
        Payment payment =
                new Payment("John", "Doe", "12345", "encrypted123", new BigDecimal("100.00"));

        assertDoesNotThrow(() -> payment.validate());
    }

    @Test
    void shouldThrowExceptionWhenFirstNameIsNull() {
        Payment payment =
                new Payment(null, "Doe", "12345", "encrypted123", new BigDecimal("100.00"));

        PaymentValidationException exception =
                assertThrows(PaymentValidationException.class, () -> payment.validate());

        assertEquals("First name is required", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenFirstNameIsBlank() {
        Payment payment =
                new Payment("  ", "Doe", "12345", "encrypted123", new BigDecimal("100.00"));

        PaymentValidationException exception =
                assertThrows(PaymentValidationException.class, () -> payment.validate());

        assertEquals("First name is required", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenLastNameIsNull() {
        Payment payment =
                new Payment("John", null, "12345", "encrypted123", new BigDecimal("100.00"));

        PaymentValidationException exception =
                assertThrows(PaymentValidationException.class, () -> payment.validate());

        assertEquals("Last name is required", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenZipCodeIsNull() {
        Payment payment =
                new Payment("John", "Doe", null, "encrypted123", new BigDecimal("100.00"));

        PaymentValidationException exception =
                assertThrows(PaymentValidationException.class, () -> payment.validate());

        assertEquals("Zip code is required", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenCardNumberIsNull() {
        Payment payment = new Payment("John", "Doe", "12345", null, new BigDecimal("100.00"));

        PaymentValidationException exception =
                assertThrows(PaymentValidationException.class, () -> payment.validate());

        assertEquals("Card number is required", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenAmountIsNull() {
        Payment payment = new Payment("John", "Doe", "12345", "encrypted123", null);

        PaymentValidationException exception =
                assertThrows(PaymentValidationException.class, () -> payment.validate());

        assertEquals("Amount must be greater than 0", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenAmountIsZero() {
        Payment payment = new Payment("John", "Doe", "12345", "encrypted123", BigDecimal.ZERO);

        PaymentValidationException exception =
                assertThrows(PaymentValidationException.class, () -> payment.validate());

        assertEquals("Amount must be greater than 0", exception.getMessage());
    }

    @Test
    void shouldMarkPaymentAsProcessed() {
        Payment payment =
                new Payment("John", "Doe", "12345", "encrypted123", new BigDecimal("100.00"));

        payment.markAsProcessed();

        assertEquals(PaymentStatus.PROCESSED, payment.getStatus());
    }

    @Test
    void shouldMarkPaymentAsFailed() {
        Payment payment =
                new Payment("John", "Doe", "12345", "encrypted123", new BigDecimal("100.00"));

        payment.markAsFailed();

        assertEquals(PaymentStatus.FAILED, payment.getStatus());
    }
}
