import { PaymentResponse } from "@/lib/api";

interface PaymentCardProps {
  payment: PaymentResponse;
}

export default function PaymentCard({ payment }: PaymentCardProps) {
  const formatDate = (dateString: string) => {
    return new Date(dateString).toLocaleDateString("en-US", {
      year: "numeric",
      month: "short",
      day: "numeric",
      hour: "2-digit",
      minute: "2-digit",
    });
  };

  const getStatusColor = (status: string) => {
    switch (status) {
      case "PROCESSED":
        return "#10b981";
      case "PENDING":
        return "#f59e0b";
      case "FAILED":
        return "#ef4444";
      default:
        return "#6b7280";
    }
  };

  return (
    <div
      style={{
        padding: "1.5rem",
        border: "1px solid #e5e7eb",
        borderRadius: "0.5rem",
        marginBottom: "1rem",
        backgroundColor: "#fff",
        transition: "all 0.2s",
        cursor: "pointer",
      }}
      onMouseEnter={(e) => {
        e.currentTarget.style.boxShadow = "0 4px 6px rgba(0, 0, 0, 0.1)";
        e.currentTarget.style.borderColor = "#667eea";
      }}
      onMouseLeave={(e) => {
        e.currentTarget.style.boxShadow = "none";
        e.currentTarget.style.borderColor = "#e5e7eb";
      }}
    >
      <div
        style={{
          display: "flex",
          justifyContent: "space-between",
          alignItems: "flex-start",
          marginBottom: "1rem",
        }}
      >
        <div>
          <h3
            style={{
              margin: 0,
              fontSize: "1.125rem",
              fontWeight: "600",
              color: "#111827",
            }}
          >
            {payment.firstName} {payment.lastName}
          </h3>
          <p
            style={{
              margin: "0.25rem 0 0 0",
              fontSize: "0.875rem",
              color: "#6b7280",
            }}
          >
            ID: {payment.id.substring(0, 8)}...
          </p>
        </div>
        <span
          style={{
            padding: "0.25rem 0.75rem",
            borderRadius: "9999px",
            fontSize: "0.75rem",
            fontWeight: "600",
            backgroundColor: `${getStatusColor(payment.status)}20`,
            color: getStatusColor(payment.status),
            textTransform: "uppercase",
          }}
        >
          {payment.status}
        </span>
      </div>

      <div
        style={{
          display: "grid",
          gridTemplateColumns: "repeat(auto-fit, minmax(150px, 1fr))",
          gap: "0.75rem",
        }}
      >
        <div>
          <p
            style={{
              margin: 0,
              fontSize: "0.75rem",
              color: "#6b7280",
              textTransform: "uppercase",
              letterSpacing: "0.05em",
            }}
          >
            Amount
          </p>
          <p
            style={{
              margin: "0.25rem 0 0 0",
              fontSize: "1rem",
              fontWeight: "600",
              color: "#10b981",
            }}
          >
            ${payment.amount?.toFixed(2) || "0.00"}
          </p>
        </div>

        <div>
          <p
            style={{
              margin: 0,
              fontSize: "0.75rem",
              color: "#6b7280",
              textTransform: "uppercase",
              letterSpacing: "0.05em",
            }}
          >
            Card Number
          </p>
          <p
            style={{
              margin: "0.25rem 0 0 0",
              fontSize: "0.875rem",
              fontWeight: "500",
              color: "#111827",
            }}
          >
            {payment.maskedCardNumber}
          </p>
        </div>

        <div>
          <p
            style={{
              margin: 0,
              fontSize: "0.75rem",
              color: "#6b7280",
              textTransform: "uppercase",
              letterSpacing: "0.05em",
            }}
          >
            ZIP Code
          </p>
          <p
            style={{
              margin: "0.25rem 0 0 0",
              fontSize: "0.875rem",
              fontWeight: "500",
              color: "#111827",
            }}
          >
            {payment.zipCode}
          </p>
        </div>

        <div>
          <p
            style={{
              margin: 0,
              fontSize: "0.75rem",
              color: "#6b7280",
              textTransform: "uppercase",
              letterSpacing: "0.05em",
            }}
          >
            Created At
          </p>
          <p
            style={{
              margin: "0.25rem 0 0 0",
              fontSize: "0.875rem",
              fontWeight: "500",
              color: "#111827",
            }}
          >
            {formatDate(payment.createdAt)}
          </p>
        </div>
      </div>
    </div>
  );
}
