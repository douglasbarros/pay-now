package com.ezycollect.server.infrastructure.persistence.adapter;

import com.ezycollect.server.domain.model.Payment;
import com.ezycollect.server.domain.repository.PaymentRepository;
import com.ezycollect.server.infrastructure.persistence.entity.PaymentEntity;
import com.ezycollect.server.infrastructure.persistence.jpa.JpaPaymentRepository;
import com.ezycollect.server.infrastructure.persistence.mapper.PaymentEntityMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Adapter implementing the PaymentRepository port using JPA.
 */
@Component
public class PaymentRepositoryAdapter implements PaymentRepository {

    private final JpaPaymentRepository jpaPaymentRepository;

    public PaymentRepositoryAdapter(JpaPaymentRepository jpaPaymentRepository) {
        this.jpaPaymentRepository = jpaPaymentRepository;
    }

    @Override
    public Payment save(Payment payment) {
        PaymentEntity entity = PaymentEntityMapper.toEntity(payment);
        PaymentEntity savedEntity = jpaPaymentRepository.save(entity);
        return PaymentEntityMapper.toDomain(savedEntity);
    }

    @Override
    public Optional<Payment> findById(UUID id) {
        return jpaPaymentRepository.findById(id)
                .map(PaymentEntityMapper::toDomain);
    }

    @Override
    public List<Payment> findAll() {
        return jpaPaymentRepository.findAll().stream()
                .map(PaymentEntityMapper::toDomain)
                .toList();
    }

    @Override
    public List<Payment> findAll(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        return jpaPaymentRepository.findAll(pageable).stream()
                .map(PaymentEntityMapper::toDomain)
                .toList();
    }

    @Override
    public long count() {
        return jpaPaymentRepository.count();
    }

    @Override
    public void deleteById(UUID id) {
        jpaPaymentRepository.deleteById(id);
    }
}
