package com.ezycollect.server.entrypoint.rest;

import com.ezycollect.server.application.dto.CreatePaymentRequest;
import com.ezycollect.server.domain.model.Payment;
import com.ezycollect.server.domain.repository.PaymentRepository;
import com.ezycollect.server.domain.service.EncryptionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class PaymentControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private EncryptionService encryptionService;

    @BeforeEach
    void setUp() {
        // Clean up before each test
    }

    @Test
    void shouldCreatePaymentSuccessfully() throws Exception {
        CreatePaymentRequest request = new CreatePaymentRequest();
        request.setFirstName("John");
        request.setLastName("Doe");
        request.setZipCode("12345");
        request.setCardNumber("4532015112830366");
        request.setAmount(new BigDecimal("100.00"));

        mockMvc.perform(post("/api/payments").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))).andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Doe"))
                .andExpect(jsonPath("$.zipCode").value("12345"))
                .andExpect(jsonPath("$.maskedCardNumber").value("************0366"))
                .andExpect(jsonPath("$.status").value("PROCESSED"));
    }

    @Test
    void shouldReturnBadRequestWhenFirstNameIsMissing() throws Exception {
        CreatePaymentRequest request = new CreatePaymentRequest();
        request.setLastName("Doe");
        request.setZipCode("12345");
        request.setCardNumber("4532015112830366");
        request.setAmount(new BigDecimal("100.00"));

        mockMvc.perform(post("/api/payments").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.message").value(containsString("First name is required")));
    }

    @Test
    void shouldReturnBadRequestWhenCardNumberIsInvalid() throws Exception {
        CreatePaymentRequest request = new CreatePaymentRequest();
        request.setFirstName("John");
        request.setLastName("Doe");
        request.setZipCode("12345");
        request.setCardNumber("123"); // Invalid
        request.setAmount(new BigDecimal("100.00"));

        mockMvc.perform(post("/api/payments").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void shouldReturnBadRequestWhenZipCodeIsInvalid() throws Exception {
        CreatePaymentRequest request = new CreatePaymentRequest();
        request.setFirstName("John");
        request.setLastName("Doe");
        request.setZipCode("abc"); // Invalid
        request.setCardNumber("4532015112830366");
        request.setAmount(new BigDecimal("100.00"));

        mockMvc.perform(post("/api/payments").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void shouldGetPaymentByIdSuccessfully() throws Exception {
        String encryptedCard = encryptionService.encrypt("4532015112830366");
        Payment payment =
                new Payment("John", "Doe", "12345", encryptedCard, new BigDecimal("100.00"));
        Payment saved = paymentRepository.save(payment);

        mockMvc.perform(get("/api/payments/" + saved.getId())).andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(saved.getId().toString()))
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Doe"));
    }

    @Test
    void shouldReturnNotFoundWhenPaymentDoesNotExist() throws Exception {
        UUID nonExistentId = UUID.randomUUID();

        mockMvc.perform(get("/api/payments/" + nonExistentId)).andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(containsString("Payment not found")));
    }

    @Test
    void shouldGetAllPaymentsSuccessfully() throws Exception {
        String encryptedCard1 = encryptionService.encrypt("4532015112830366");
        String encryptedCard2 = encryptionService.encrypt("5425233430109903");
        Payment payment1 =
                new Payment("John", "Doe", "12345", encryptedCard1, new BigDecimal("100.00"));
        Payment payment2 =
                new Payment("Jane", "Smith", "67890", encryptedCard2, new BigDecimal("50.00"));
        paymentRepository.save(payment1);
        paymentRepository.save(payment2);

        mockMvc.perform(get("/api/payments")).andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(2))))
                .andExpect(jsonPath("$[*].firstName", hasItem("John")))
                .andExpect(jsonPath("$[*].firstName", hasItem("Jane")));
    }
}
