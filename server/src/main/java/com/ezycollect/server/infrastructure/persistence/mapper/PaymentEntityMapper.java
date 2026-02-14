package com.ezycollect.server.infrastructure.persistence.mapper;

import com.ezycollect.server.domain.model.Payment;
import com.ezycollect.server.domain.model.PaymentStatus;
import com.ezycollect.server.infrastructure.persistence.entity.PaymentEntity;
import com.ezycollect.server.infrastructure.persistence.entity.PaymentStatusEntity;

/**
 * Mapper between Payment domain model and PaymentEntity.
 */
public class PaymentEntityMapper {

    private PaymentEntityMapper() {
        // Private constructor to prevent instantiation
    }

    public static PaymentEntity toEntity(Payment payment) {
        return PaymentEntity.builder().id(payment.getId()).firstName(payment.getFirstName())
                .lastName(payment.getLastName()).zipCode(payment.getZipCode())
                .encryptedCardNumber(payment.getEncryptedCardNumber()).amount(payment.getAmount())
                .createdAt(payment.getCreatedAt()).status(toStatusEntity(payment.getStatus()))
                .build();
    }

    public static Payment toDomain(PaymentEntity entity) {
        Payment payment = new Payment();
        payment.setId(entity.getId());
        payment.setFirstName(entity.getFirstName());
        payment.setLastName(entity.getLastName());
        payment.setZipCode(entity.getZipCode());
        payment.setEncryptedCardNumber(entity.getEncryptedCardNumber());
        payment.setAmount(entity.getAmount());
        payment.setCreatedAt(entity.getCreatedAt());
        payment.setStatus(toStatusDomain(entity.getStatus()));
        return payment;
    }

    private static PaymentStatusEntity toStatusEntity(PaymentStatus status) {
        return PaymentStatusEntity.valueOf(status.name());
    }

    private static PaymentStatus toStatusDomain(PaymentStatusEntity status) {
        return PaymentStatus.valueOf(status.name());
    }
}
