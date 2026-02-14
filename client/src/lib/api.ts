import axios from "axios";

const API_BASE_URL =
  process.env.NEXT_PUBLIC_API_URL || "http://localhost:8080/api";

// Payment status enum matching backend
export enum PaymentStatus {
  PENDING = "PENDING",
  PROCESSED = "PROCESSED",
  FAILED = "FAILED",
}

export interface CreatePaymentRequest {
  firstName: string;
  lastName: string;
  zipCode: string;
  cardNumber: string;
  amount: number;
}

export interface PaymentResponse {
  id: string;
  firstName: string;
  lastName: string;
  zipCode: string;
  maskedCardNumber: string;
  amount: number;
  status: PaymentStatus;
  createdAt: string;
}

export interface PageResponse<T> {
  content: T[];
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
  first: boolean;
  last: boolean;
}

export interface RegisterWebhookRequest {
  endpointUrl: string;
}

export interface WebhookResponse {
  id: string;
  endpointUrl: string;
  active: boolean;
  createdAt: string;
  updatedAt: string;
}

export interface ErrorResponse {
  timestamp: string;
  status: number;
  error: string;
  message: string;
  path: string;
}

const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    "Content-Type": "application/json",
  },
});

// Payment API
export const paymentApi = {
  create: async (data: CreatePaymentRequest): Promise<PaymentResponse> => {
    const response = await api.post("/payments", data);
    return response.data;
  },

  getById: async (id: string): Promise<PaymentResponse> => {
    const response = await api.get(`/payments/${id}`);
    return response.data;
  },

  getAll: async (): Promise<PaymentResponse[]> => {
    const response = await api.get("/payments");
    return response.data;
  },

  getPaginated: async (
    page: number,
    size: number,
  ): Promise<PageResponse<PaymentResponse>> => {
    const response = await api.get("/payments", {
      params: { page, size },
    });
    return response.data;
  },
};

// Webhook API
export const webhookApi = {
  register: async (data: RegisterWebhookRequest): Promise<WebhookResponse> => {
    const response = await api.post("/webhooks", data);
    return response.data;
  },

  getById: async (id: string): Promise<WebhookResponse> => {
    const response = await api.get(`/webhooks/${id}`);
    return response.data;
  },

  getAll: async (): Promise<WebhookResponse[]> => {
    const response = await api.get("/webhooks");
    return response.data;
  },

  delete: async (id: string): Promise<void> => {
    await api.delete(`/webhooks/${id}`);
  },

  activate: async (id: string): Promise<WebhookResponse> => {
    const response = await api.patch(`/webhooks/${id}/activate`);
    return response.data;
  },

  deactivate: async (id: string): Promise<WebhookResponse> => {
    const response = await api.patch(`/webhooks/${id}/deactivate`);
    return response.data;
  },
};

export default api;
