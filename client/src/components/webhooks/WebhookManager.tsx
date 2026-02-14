"use client";

import { useState, useEffect } from "react";
import { webhookApi, type WebhookResponse } from "@/lib/api";

export default function WebhookManager() {
  const [webhookUrl, setWebhookUrl] = useState("");
  const [webhooks, setWebhooks] = useState<WebhookResponse[]>([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [success, setSuccess] = useState<string | null>(null);

  const loadWebhooks = async () => {
    try {
      const data = await webhookApi.getAll();
      setWebhooks(data);
    } catch (err: any) {
      setError("Failed to load webhooks");
    }
  };

  const handleRegister = async (e: React.FormEvent) => {
    e.preventDefault();
    setLoading(true);
    setError(null);
    setSuccess(null);

    try {
      await webhookApi.register({ endpointUrl: webhookUrl });
      setSuccess("Webhook registered successfully!");
      setWebhookUrl("");
      loadWebhooks();
    } catch (err: any) {
      setError(err.response?.data?.message || "Failed to register webhook");
    } finally {
      setLoading(false);
    }
  };

  const handleDelete = async (id: string) => {
    if (!confirm("Are you sure you want to delete this webhook?")) return;

    try {
      await webhookApi.delete(id);
      setSuccess("Webhook deleted successfully!");
      loadWebhooks();
    } catch (err: any) {
      setError("Failed to delete webhook");
    }
  };

  const handleToggle = async (id: string, active: boolean) => {
    try {
      if (active) {
        await webhookApi.deactivate(id);
      } else {
        await webhookApi.activate(id);
      }
      loadWebhooks();
    } catch (err: any) {
      setError("Failed to update webhook");
    }
  };

  // Load webhooks on mount
  useEffect(() => {
    loadWebhooks();
  }, []);

  return (
    <>
      <h2>Manage Webhooks</h2>

      {success && (
        <div className="success-message">
          <strong>✓ {success}</strong>
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

      <form onSubmit={handleRegister} style={{ marginBottom: "2rem" }}>
        <div className="form-group">
          <label>Webhook Endpoint URL</label>
          <input
            type="url"
            value={webhookUrl}
            onChange={(e) => setWebhookUrl(e.target.value)}
            required
            placeholder="https://your-webhook-endpoint.com/webhook"
            pattern="^https?://.*"
          />
          <span className="error-message">
            Must start with http:// or https://
          </span>
        </div>

        <button type="submit" className="button" disabled={loading}>
          {loading ? "Registering..." : "Register Webhook"}
        </button>
      </form>

      <div>
        <h3 style={{ marginBottom: "1rem" }}>Registered Webhooks</h3>
        {webhooks.length === 0 ? (
          <p style={{ color: "#666", textAlign: "center", padding: "2rem" }}>
            No webhooks registered yet. Add one above!
          </p>
        ) : (
          <table className="table">
            <thead>
              <tr>
                <th>Endpoint URL</th>
                <th>Status</th>
                <th>Created</th>
                <th>Actions</th>
              </tr>
            </thead>
            <tbody>
              {webhooks.map((webhook) => (
                <tr key={webhook.id}>
                  <td
                    style={{
                      maxWidth: "300px",
                      overflow: "hidden",
                      textOverflow: "ellipsis",
                    }}
                  >
                    {webhook.endpointUrl}
                  </td>
                  <td>
                    <span
                      className={`badge ${webhook.active ? "success" : "danger"}`}
                    >
                      {webhook.active ? "Active" : "Inactive"}
                    </span>
                  </td>
                  <td>{new Date(webhook.createdAt).toLocaleDateString()}</td>
                  <td>
                    <div className="action-buttons">
                      <button
                        onClick={() => handleToggle(webhook.id, webhook.active)}
                        className={`button ${webhook.active ? "secondary" : ""}`}
                        style={{ fontSize: "0.875rem", padding: "0.5rem 1rem" }}
                      >
                        {webhook.active ? "Deactivate" : "Activate"}
                      </button>
                      <button
                        onClick={() => handleDelete(webhook.id)}
                        className="button danger"
                        style={{ fontSize: "0.875rem", padding: "0.5rem 1rem" }}
                      >
                        Delete
                      </button>
                    </div>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        )}
      </div>
    </>
  );
}
