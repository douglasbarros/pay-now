package com.ezycollect.server.domain.repository;

import com.ezycollect.server.domain.model.Payment;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Port (interface) for payment persistence operations.
 * This defines the contract that infrastructure adapters must implement.
 */
public interface PaymentRepository {

    /**
     * Saves a payment to the persistence layer.
     *
     * @param payment the payment to save
     * @return the saved payment
     */
    Payment save(Payment payment);

    /**
     * Finds a payment by its unique identifier.
     *
     * @param id the payment ID
     * @return an Optional containing the payment if found
     */
    Optional<Payment> findById(UUID id);

    /**
     * Retrieves all payments.
     *
     * @return list of all payments
     */
    List<Payment> findAll();

    /**
     * Retrieves a page of payments.
     *
     * @param page the page number (0-indexed)
     * @param size the page size
     * @return list of payments for the requested page
     */
    List<Payment> findAll(int page, int size);

    /**
     * Counts the total number of payments.
     *
     * @return total count of payments
     */
    long count();

    /**
     * Deletes a payment by its ID.
     *
     * @param id the payment ID
     */
    void deleteById(UUID id);
}
