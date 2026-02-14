package com.ezycollect.server.domain.model;

/**
 * Represents the response from a payment gateway.
 */
public class PaymentGatewayResponse {

    private final boolean success;
    private final String errorMessage;
    private final String transactionId;

    public PaymentGatewayResponse(boolean success, String errorMessage, String transactionId) {
        this.success = success;
        this.errorMessage = errorMessage;
        this.transactionId = transactionId;
    }

    public static PaymentGatewayResponse success(String transactionId) {
        return new PaymentGatewayResponse(true, null, transactionId);
    }

    public static PaymentGatewayResponse failure(String errorMessage) {
        return new PaymentGatewayResponse(false, errorMessage, null);
    }

    public boolean isSuccess() {
        return success;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public String getTransactionId() {
        return transactionId;
    }
}
