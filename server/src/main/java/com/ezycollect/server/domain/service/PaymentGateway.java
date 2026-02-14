package com.ezycollect.server.domain.service;

import com.ezycollect.server.domain.model.Payment;
import com.ezycollect.server.domain.model.PaymentGatewayResponse;

/**
 * Port for payment gateway integration. Implementations should handle communication with external
 * payment processors.
 */
public interface PaymentGateway {

    /**
     * Processes a payment through the payment gateway.
     *
     * @param payment the payment to process
     * @return the gateway response indicating success or failure
     */
    PaymentGatewayResponse process(Payment payment);
}
