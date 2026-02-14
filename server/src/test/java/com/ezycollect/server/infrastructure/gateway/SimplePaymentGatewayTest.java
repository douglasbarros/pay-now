package com.ezycollect.server.infrastructure.gateway;

import com.ezycollect.server.domain.model.Payment;
import com.ezycollect.server.domain.model.PaymentGatewayResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class SimplePaymentGatewayTest {

    private SimplePaymentGateway paymentGateway;

    @BeforeEach
    void setUp() {
        paymentGateway = new SimplePaymentGateway();
    }

    @Test
    void shouldProcessPaymentSuccessfully() {
        Payment payment =
                new Payment("John", "Doe", "12345", "encrypted123", new BigDecimal("100.00"));

        PaymentGatewayResponse response = paymentGateway.process(payment);

        assertTrue(response.isSuccess());
        assertNull(response.getErrorMessage());
        assertNotNull(response.getTransactionId());
        assertTrue(response.getTransactionId().startsWith("TXN-"));
    }

    @Test
    void shouldRejectPaymentWithBlockedZipCode() {
        Payment payment =
                new Payment("John", "Doe", "11111", "encrypted123", new BigDecimal("100.00"));

        PaymentGatewayResponse response = paymentGateway.process(payment);

        assertFalse(response.isSuccess());
        assertEquals("Payment gateway error: blocked zip code", response.getErrorMessage());
        assertNull(response.getTransactionId());
    }

    @Test
    void shouldProcessPaymentWithDifferentZipCodes() {
        String[] validZipCodes = {"12345", "54321", "99999", "00001", "11110"};

        for (String zipCode : validZipCodes) {
            Payment payment =
                    new Payment("John", "Doe", zipCode, "encrypted123", new BigDecimal("100.00"));
            PaymentGatewayResponse response = paymentGateway.process(payment);

            assertTrue(response.isSuccess(), "Payment with zipCode " + zipCode + " should succeed");
            assertNotNull(response.getTransactionId());
        }
    }

    @Test
    void shouldGenerateUniqueTransactionIds() {
        Payment payment1 =
                new Payment("John", "Doe", "12345", "encrypted123", new BigDecimal("100.00"));
        Payment payment2 =
                new Payment("Jane", "Smith", "54321", "encrypted456", new BigDecimal("200.00"));

        PaymentGatewayResponse response1 = paymentGateway.process(payment1);
        PaymentGatewayResponse response2 = paymentGateway.process(payment2);

        assertTrue(response1.isSuccess());
        assertTrue(response2.isSuccess());
        assertNotEquals(response1.getTransactionId(), response2.getTransactionId());
    }
}
