package com.ezycollect.server.infrastructure.persistence.jpa;

import com.ezycollect.server.infrastructure.persistence.entity.PaymentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

/**
 * Spring Data JPA repository for PaymentEntity.
 */
@Repository
public interface JpaPaymentRepository extends JpaRepository<PaymentEntity, UUID> {
}
