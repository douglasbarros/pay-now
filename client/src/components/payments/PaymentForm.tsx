"use client";

import { useState } from "react";
import {
  paymentApi,
  type CreatePaymentRequest,
  type PaymentResponse,
} from "@/lib/api";

export default function PaymentForm() {
  const [formData, setFormData] = useState<CreatePaymentRequest>({
    firstName: "",
    lastName: "",
    zipCode: "",
    cardNumber: "",
    amount: 0,
  });
  const [loading, setLoading] = useState(false);
  const [success, setSuccess] = useState<PaymentResponse | null>(null);
  const [error, setError] = useState<string | null>(null);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setLoading(true);
    setError(null);
    setSuccess(null);

    try {
      const response = await paymentApi.create(formData);
      setSuccess(response);
      setFormData({
        firstName: "",
        lastName: "",
        zipCode: "",
        cardNumber: "",
        amount: 0,
      });
    } catch (err: any) {
      setError(err.response?.data?.message || "Failed to create payment");
    } finally {
      setLoading(false);
    }
  };

  return (
    <>
      <h2>Create New Payment</h2>

      {success && (
        <div className="success-message">
          <strong>✓ Payment Created Successfully!</strong>
          <p>Payment ID: {success.id}</p>
          <p>Status: {success.status}</p>
          <p>Amount: ${success.amount?.toFixed(2)}</p>
          <p>Card: {success.maskedCardNumber}</p>
        </div>
      )}

      {error && (
        <div
          style={{
            background: "#f8d7da",
            padding: "1rem",
            borderRadius: "8px",
            marginBottom: "1rem",
            color: "#721c24",
          }}
        >
          <strong>✗ Error:</strong> {error}
        </div>
      )}

      <form onSubmit={handleSubmit}>
        <div className="form-group">
          <label>First Name</label>
          <input
            type="text"
            value={formData.firstName}
            onChange={(e) =>
              setFormData({ ...formData, firstName: e.target.value })
            }
            required
            placeholder="John"
          />
        </div>

        <div className="form-group">
          <label>Last Name</label>
          <input
            type="text"
            value={formData.lastName}
            onChange={(e) =>
              setFormData({ ...formData, lastName: e.target.value })
            }
            required
            placeholder="Doe"
          />
        </div>

        <div className="form-group">
          <label>Zip Code</label>
          <input
            type="text"
            value={formData.zipCode}
            onChange={(e) =>
              setFormData({ ...formData, zipCode: e.target.value })
            }
            required
            placeholder="12345"
            pattern="^\d{5}(-\d{4})?$"
          />
          <span className="error-message">Format: 12345 or 12345-6789</span>
        </div>

        <div className="form-group">
          <label>Amount</label>
          <input
            type="number"
            value={formData.amount || ""}
            onChange={(e) =>
              setFormData({
                ...formData,
                amount: parseFloat(e.target.value) || 0,
              })
            }
            required
            placeholder="100.00"
            step="0.01"
            min="0.01"
          />
          <span className="error-message">Enter payment amount in USD</span>
        </div>

        <div className="form-group">
          <label>Card Number</label>
          <input
            type="text"
            value={formData.cardNumber}
            onChange={(e) =>
              setFormData({
                ...formData,
                cardNumber: e.target.value.replace(/\D/g, ""),
              })
            }
            required
            placeholder="1234567890123456"
            pattern="^\d{13,19}$"
            maxLength={19}
          />
          <span className="error-message">13-19 digits only</span>
        </div>

        <button type="submit" className="button" disabled={loading}>
          {loading ? "Processing..." : "Create Payment"}
        </button>
      </form>
    </>
  );
}
