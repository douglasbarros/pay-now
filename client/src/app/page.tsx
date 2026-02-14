"use client";

import { useState } from "react";
import PaymentForm from "@/components/payments/PaymentForm";
import PaymentsListPage from "@/components/payments/PaymentsListPage";
import WebhookManager from "@/components/webhooks/WebhookManager";

export default function Home() {
  const [activeTab, setActiveTab] = useState<"payment" | "list" | "webhook">(
    "payment",
  );

  return (
    <div className="container">
      <header className="header">
        <h1>💳 PayNow</h1>
        <p>Simple Payment Application with Webhook Support</p>
      </header>

      <div className="card">
        <div
          style={{
            display: "flex",
            gap: "1rem",
            marginBottom: "2rem",
            borderBottom: "2px solid #e0e0e0",
          }}
        >
          <button
            onClick={() => setActiveTab("payment")}
            style={{
              padding: "1rem 2rem",
              border: "none",
              background: "none",
              cursor: "pointer",
              borderBottom:
                activeTab === "payment" ? "3px solid #667eea" : "none",
              fontWeight: activeTab === "payment" ? "600" : "normal",
              color: activeTab === "payment" ? "#667eea" : "#666",
            }}
          >
            Create Payment
          </button>
          <button
            onClick={() => setActiveTab("list")}
            style={{
              padding: "1rem 2rem",
              border: "none",
              background: "none",
              cursor: "pointer",
              borderBottom: activeTab === "list" ? "3px solid #667eea" : "none",
              fontWeight: activeTab === "list" ? "600" : "normal",
              color: activeTab === "list" ? "#667eea" : "#666",
            }}
          >
            View Payments
          </button>
          <button
            onClick={() => setActiveTab("webhook")}
            style={{
              padding: "1rem 2rem",
              border: "none",
              background: "none",
              cursor: "pointer",
              borderBottom:
                activeTab === "webhook" ? "3px solid #667eea" : "none",
              fontWeight: activeTab === "webhook" ? "600" : "normal",
              color: activeTab === "webhook" ? "#667eea" : "#666",
            }}
          >
            Manage Webhooks
          </button>
        </div>

        {activeTab === "payment" ? (
          <PaymentForm />
        ) : activeTab === "list" ? (
          <PaymentsListPage />
        ) : (
          <WebhookManager />
        )}
      </div>
    </div>
  );
}
