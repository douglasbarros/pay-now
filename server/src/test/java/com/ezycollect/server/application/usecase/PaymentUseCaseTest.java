package com.ezycollect.server.application.usecase;

import com.ezycollect.server.application.dto.CreatePaymentRequest;
import com.ezycollect.server.application.dto.PaymentResponse;
import com.ezycollect.server.domain.exception.PaymentNotFoundException;
import com.ezycollect.server.domain.exception.PaymentProcessingException;
import com.ezycollect.server.domain.exception.PaymentValidationException;
import com.ezycollect.server.domain.model.Payment;
import com.ezycollect.server.domain.model.PaymentGatewayResponse;
import com.ezycollect.server.domain.model.PaymentStatus;
import com.ezycollect.server.domain.repository.PaymentRepository;
import com.ezycollect.server.domain.service.EncryptionService;
import com.ezycollect.server.domain.service.PaymentGateway;
import com.ezycollect.server.domain.service.WebhookNotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentUseCaseTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private EncryptionService encryptionService;

    @Mock
    private WebhookNotificationService webhookNotificationService;

    @Mock
    private PaymentGateway paymentGateway;

    private PaymentUseCase paymentUseCase;

    @BeforeEach
    void setUp() {
        paymentUseCase = new PaymentUseCase(paymentRepository, encryptionService,
                webhookNotificationService, paymentGateway);
    }

    @Test
    void shouldCreatePaymentSuccessfully() {
        CreatePaymentRequest request = new CreatePaymentRequest();
        request.setFirstName("John");
        request.setLastName("Doe");
        request.setZipCode("12345");
        request.setCardNumber("4532015112830366");
        request.setAmount(new BigDecimal("100.00"));

        Payment savedPayment =
                new Payment("John", "Doe", "12345", "encrypted123", new BigDecimal("100.00"));
        savedPayment.setId(UUID.randomUUID());

        when(encryptionService.encrypt("4532015112830366")).thenReturn("encrypted123");
        when(paymentRepository.save(any(Payment.class))).thenReturn(savedPayment);
        when(paymentGateway.process(any(Payment.class)))
                .thenReturn(PaymentGatewayResponse.success("TXN-123"));

        PaymentResponse response = paymentUseCase.createPayment(request);

        assertNotNull(response);
        assertEquals("John", response.getFirstName());
        assertEquals("Doe", response.getLastName());
        assertEquals("12345", response.getZipCode());
        assertEquals("************0366", response.getMaskedCardNumber());
        assertEquals("PROCESSED", response.getStatus());

        verify(encryptionService).encrypt("4532015112830366");
        // Repository.save is called 3 times: PENDING -> PROCESSING -> PROCESSED
        verify(paymentRepository, times(3)).save(any(Payment.class));
        verify(paymentGateway).process(any(Payment.class));
        verify(webhookNotificationService).notifyPaymentCreated(any(Payment.class));
        verify(webhookNotificationService, never()).notifyPaymentFailed(any(Payment.class),
                anyString());
    }

    @Test
    void shouldHandlePaymentProcessingFailure() {
        CreatePaymentRequest request = new CreatePaymentRequest();
        request.setFirstName("John");
        request.setLastName("Doe");
        request.setZipCode("12345");
        request.setCardNumber("4532015112830366");
        request.setAmount(new BigDecimal("100.00"));

        Payment savedPayment =
                new Payment("John", "Doe", "12345", "encrypted123", new BigDecimal("100.00"));
        savedPayment.setId(UUID.randomUUID());

        when(encryptionService.encrypt("4532015112830366")).thenReturn("encrypted123");
        when(paymentRepository.save(any(Payment.class))).thenReturn(savedPayment);
        when(paymentGateway.process(any(Payment.class)))
                .thenReturn(PaymentGatewayResponse.failure("Gateway error"));

        PaymentProcessingException exception = assertThrows(PaymentProcessingException.class,
                () -> paymentUseCase.createPayment(request));

        assertTrue(exception.getMessage().contains("Payment gateway error"));
        verify(encryptionService).encrypt("4532015112830366");
        verify(paymentRepository, times(3)).save(any(Payment.class)); // PENDING, PROCESSING, FAILED
        verify(paymentGateway).process(any(Payment.class));
        verify(webhookNotificationService).notifyPaymentFailed(any(Payment.class), anyString());
        verify(webhookNotificationService, never()).notifyPaymentCreated(any(Payment.class));
    }

    @Test
    void shouldThrowExceptionWhenPaymentValidationFails() {
        CreatePaymentRequest request = new CreatePaymentRequest();
        request.setFirstName(null);
        request.setLastName("Doe");
        request.setZipCode("12345");
        request.setCardNumber("4532015112830366");
        request.setAmount(new BigDecimal("100.00"));

        when(encryptionService.encrypt("4532015112830366")).thenReturn("encrypted123");

        assertThrows(PaymentValidationException.class, () -> paymentUseCase.createPayment(request));

        verify(paymentRepository, never()).save(any(Payment.class));
        verify(webhookNotificationService, never()).notifyPaymentCreated(any(Payment.class));
    }

    @Test
    void shouldGetPaymentByIdSuccessfully() {
        UUID paymentId = UUID.randomUUID();
        Payment payment =
                new Payment("John", "Doe", "12345", "encrypted123", new BigDecimal("100.00"));
        payment.setId(paymentId);
        payment.setStatus(PaymentStatus.PROCESSED);

        when(paymentRepository.findById(paymentId)).thenReturn(Optional.of(payment));
        when(encryptionService.decrypt("encrypted123")).thenReturn("4532015112830366");

        PaymentResponse response = paymentUseCase.getPaymentById(paymentId);

        assertNotNull(response);
        assertEquals(paymentId, response.getId());
        assertEquals("John", response.getFirstName());
        assertEquals("************0366", response.getMaskedCardNumber());

        verify(paymentRepository).findById(paymentId);
        verify(encryptionService).decrypt("encrypted123");
    }

    @Test
    void shouldThrowExceptionWhenPaymentNotFound() {
        UUID paymentId = UUID.randomUUID();

        when(paymentRepository.findById(paymentId)).thenReturn(Optional.empty());

        PaymentNotFoundException exception = assertThrows(PaymentNotFoundException.class,
                () -> paymentUseCase.getPaymentById(paymentId));

        assertTrue(exception.getMessage().contains("Payment not found"));
        verify(paymentRepository).findById(paymentId);
    }

    @Test
    void shouldGetAllPaymentsSuccessfully() {
        Payment payment1 =
                new Payment("John", "Doe", "12345", "encrypted123", new BigDecimal("100.00"));
        payment1.setId(UUID.randomUUID());
        Payment payment2 =
                new Payment("Jane", "Smith", "67890", "encrypted456", new BigDecimal("50.00"));
        payment2.setId(UUID.randomUUID());

        when(paymentRepository.findAll()).thenReturn(List.of(payment1, payment2));
        when(encryptionService.decrypt("encrypted123")).thenReturn("4532015112830366");
        when(encryptionService.decrypt("encrypted456")).thenReturn("5425233430109903");

        List<PaymentResponse> responses = paymentUseCase.getAllPayments();

        assertEquals(2, responses.size());
        assertEquals("John", responses.get(0).getFirstName());
        assertEquals("Jane", responses.get(1).getFirstName());

        verify(paymentRepository).findAll();
        verify(encryptionService, times(2)).decrypt(anyString());
    }

    @Test
    void shouldReturnEmptyListWhenNoPaymentsExist() {
        when(paymentRepository.findAll()).thenReturn(List.of());

        List<PaymentResponse> responses = paymentUseCase.getAllPayments();

        assertTrue(responses.isEmpty());
        verify(paymentRepository).findAll();
    }

    @Test
    void shouldFailPaymentWhenZipCodeIs11111() {
        CreatePaymentRequest request = new CreatePaymentRequest();
        request.setFirstName("John");
        request.setLastName("Doe");
        request.setZipCode("11111");
        request.setCardNumber("4532015112830366");
        request.setAmount(new BigDecimal("100.00"));

        Payment savedPayment =
                new Payment("John", "Doe", "11111", "encrypted123", new BigDecimal("100.00"));
        savedPayment.setId(UUID.randomUUID());

        when(encryptionService.encrypt("4532015112830366")).thenReturn("encrypted123");
        when(paymentRepository.save(any(Payment.class))).thenReturn(savedPayment);
        when(paymentGateway.process(any(Payment.class))).thenReturn(
                PaymentGatewayResponse.failure("Payment gateway error: blocked zip code"));

        PaymentProcessingException exception = assertThrows(PaymentProcessingException.class,
                () -> paymentUseCase.createPayment(request));

        assertTrue(exception.getMessage().contains("blocked zip code"));
        verify(paymentGateway).process(any(Payment.class));
        verify(webhookNotificationService).notifyPaymentFailed(any(Payment.class), anyString());
        verify(webhookNotificationService, never()).notifyPaymentCreated(any(Payment.class));
    }

    @Test
    void shouldDetectFraudWhenFirstNameAndLastNameAreAaa() {
        CreatePaymentRequest request = new CreatePaymentRequest();
        request.setFirstName("aaa");
        request.setLastName("aaa");
        request.setZipCode("12345");
        request.setCardNumber("4532015112830366");
        request.setAmount(new BigDecimal("100.00"));

        Payment savedPayment =
                new Payment("aaa", "aaa", "12345", "encrypted123", new BigDecimal("100.00"));
        savedPayment.setId(UUID.randomUUID());

        when(encryptionService.encrypt("4532015112830366")).thenReturn("encrypted123");
        when(paymentRepository.save(any(Payment.class))).thenReturn(savedPayment);

        PaymentProcessingException exception = assertThrows(PaymentProcessingException.class,
                () -> paymentUseCase.createPayment(request));

        assertTrue(exception.getMessage().contains("Fraud detected"));
        verify(paymentGateway, never()).process(any(Payment.class)); // Gateway not called due to
                                                                     // fraud
        verify(webhookNotificationService).notifyPaymentFailed(any(Payment.class), anyString());
        verify(webhookNotificationService, never()).notifyPaymentCreated(any(Payment.class));
    }

    @Test
    void shouldProcessPaymentSuccessfullyWithValidZipCode() {
        CreatePaymentRequest request = new CreatePaymentRequest();
        request.setFirstName("Jane");
        request.setLastName("Smith");
        request.setZipCode("54321");
        request.setCardNumber("5425233430109903");
        request.setAmount(new BigDecimal("250.00"));

        Payment savedPayment =
                new Payment("Jane", "Smith", "54321", "encrypted456", new BigDecimal("250.00"));
        savedPayment.setId(UUID.randomUUID());

        when(encryptionService.encrypt("5425233430109903")).thenReturn("encrypted456");
        when(paymentRepository.save(any(Payment.class))).thenReturn(savedPayment);
        when(paymentGateway.process(any(Payment.class)))
                .thenReturn(PaymentGatewayResponse.success("TXN-456"));

        PaymentResponse response = paymentUseCase.createPayment(request);

        assertNotNull(response);
        assertEquals("Jane", response.getFirstName());
        assertEquals("Smith", response.getLastName());
        assertEquals("54321", response.getZipCode());
        assertEquals("PROCESSED", response.getStatus());

        verify(paymentGateway).process(any(Payment.class));
        verify(webhookNotificationService).notifyPaymentCreated(any(Payment.class));
        verify(webhookNotificationService, never()).notifyPaymentFailed(any(Payment.class),
                anyString());
    }
}
