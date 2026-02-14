package com.ezycollect.server.domain.service;

import com.ezycollect.server.domain.model.Payment;

/**
 * Port (interface) for webhook notification operations. This defines the contract that
 * infrastructure adapters must implement.
 */
public interface WebhookNotificationService {

    /**
     * Notifies all active webhooks about a payment event.
     *
     * @param payment the payment to notify about
     */
    void notifyPaymentCreated(Payment payment);

    /**
     * Notifies all active webhooks about a payment failure.
     *
     * @param payment the payment that failed
     * @param errorMessage the error message describing the failure
     */
    void notifyPaymentFailed(Payment payment, String errorMessage);
}
