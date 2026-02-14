package com.ezycollect.server.infrastructure.gateway;

import com.ezycollect.server.domain.model.Payment;
import com.ezycollect.server.domain.model.PaymentGatewayResponse;
import com.ezycollect.server.domain.service.PaymentGateway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * Simple payment gateway implementation that simulates payment processing. Always returns success
 * except when zipCode = "11111".
 */
@Service
public class SimplePaymentGateway implements PaymentGateway {

    private static final Logger logger = LoggerFactory.getLogger(SimplePaymentGateway.class);
    private static final String BLOCKED_ZIP_CODE = "11111";

    @Override
    public PaymentGatewayResponse process(Payment payment) {
        logger.info("Processing payment for {} {} with zipCode {}", payment.getFirstName(),
                payment.getLastName(), payment.getZipCode());

        // Simulate payment gateway logic
        if (BLOCKED_ZIP_CODE.equals(payment.getZipCode())) {
            logger.warn("Payment rejected: blocked zip code {}", payment.getZipCode());
            return PaymentGatewayResponse.failure("Payment gateway error: blocked zip code");
        }

        // Simulate successful processing
        String transactionId = "TXN-" + UUID.randomUUID().toString();
        logger.info("Payment processed successfully with transaction ID: {}", transactionId);
        return PaymentGatewayResponse.success(transactionId);
    }
}
