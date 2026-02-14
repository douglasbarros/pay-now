# 💳 PayNow - Payment Application

A modern payment application built with **Java 21**, **Spring Boot**, **React**, **Next.js**, and **MySQL**, following **Clean Code**, **Hexagonal Architecture**, and **SOLID principles**.

## 🎯 Features

### Payment Management
- ✅ Create payments with encrypted card numbers
- ✅ Payment amount field with decimal precision (up to $99,999,999.99)
- ✅ Payment gateway integration with configurable behavior
- ✅ Fraud detection logic
- ✅ Retrieve payment information
- ✅ Card number encryption using AES
- ✅ Masked card number display
- ✅ Amount validation (minimum $0.01)

### Webhook System
- ✅ Register dynamic webhook endpoints
- ✅ Automatic webhook notifications on payment creation
- ✅ Resilient webhook delivery with retry mechanism
- ✅ Webhook activation/deactivation
- ✅ Exponential backoff retry (3 attempts: 2s, 4s, 8s)

### API Documentation
- ✅ OpenAPI 3.0 specification with examples
- ✅ Swagger UI available at `/swagger-ui.html`
- ✅ API documentation at `/api-docs`

## 🏗️ Architecture

The application follows **Hexagonal Architecture (Ports & Adapters)** with clear separation of concerns:

```
server/
├── domain/              # Business logic (framework-agnostic)
│   ├── model/          # Domain entities
│   ├── repository/     # Repository ports (interfaces)
│   └── service/        # Service ports (interfaces)
│
├── application/         # Use cases and orchestration
│   ├── usecase/        # Business use cases
│   ├── dto/            # Data Transfer Objects
│   └── mapper/         # Domain-DTO mappers
│
├── infrastructure/      # External implementations
│   ├── persistence/    # JPA adapters
│   ├── encryption/     # Encryption service
│   ├── gateway/        # Payment gateway adapter
│   ├── webhook/        # Webhook notification service
│   └── config/         # Spring configurations
│
└── entrypoint/         # External interfaces
    └── rest/           # REST controllers
```

### Key Design Principles

- **Single Responsibility**: Each class has one reason to change
- **Open/Closed**: Extend behavior via abstractions, not modifications
- **Liskov Substitution**: Implementations respect interface contracts
- **Interface Segregation**: Small, focused interfaces
- **Dependency Inversion**: High-level modules depend on abstractions

## �️ Exception Handling

The application implements a robust exception hierarchy following **Domain-Driven Design** principles:

### Domain Exception Hierarchy

```
PaymentException (abstract)
├── PaymentValidationException (422)
├── PaymentProcessingException (422)
└── PaymentNotFoundException (404)

WebhookException (abstract)
├── WebhookValidationException (422)
└── WebhookNotFoundException (404)
```

### Exception Mapping

Domain exceptions are automatically mapped to appropriate HTTP codes:

| Exception | HTTP Status | When It Occurs |
|-----------|-------------|----------------|
| `PaymentValidationException` | 422 Unprocessable Entity | Invalid payment data (missing fields, invalid format) |
| `PaymentProcessingException` | 422 Unprocessable Entity | Payment processing failure (gateway error, fraud detection) |
| `PaymentNotFoundException` | 404 Not Found | Payment ID not found in database |
| `WebhookValidationException` | 422 Unprocessable Entity | Invalid webhook URL format |
| `WebhookNotFoundException` | 404 Not Found | Webhook ID not found in database |

### Payment Processing Flow

The payment creation follows a clear state machine with proper error handling:

1. **PENDING** → Payment created and saved
2. **PROCESSING** → Payment marked as being processed
3. **Fraud Detection** → Validates customer information
4. **Gateway Processing** → Payment gateway processes the transaction
5. **PROCESSED** ✅ or **FAILED** ❌ → Final state based on processing result
6. **Webhook Notification** → Active webhooks notified of success or failure

**Error Handling:**
- Fraud detection rejects suspicious transactions (e.g., firstName="aaa" && lastName="aaa")
- Payment gateway may reject transactions based on business rules (e.g., blocked zip codes)
- If processing fails, payment is marked as `FAILED`
- Webhook receives failure notification with error message
- `PaymentProcessingException` is thrown with error details

### Error Response Format

All errors return a consistent JSON structure:

```json
{
  "status": 422,
  "error": "Payment Validation Error",
  "message": "First name is required",
  "path": "/api/payments"
}
```

### Design Benefits

- **Expressiveness**: Domain exceptions clearly communicate business errors
- **Maintainability**: Centralized exception handling in `GlobalExceptionHandler`
- **Type Safety**: Compile-time validation of exception handling
- **Testability**: Easy to test exception scenarios with specific exception types
- **Clean Architecture**: Exceptions defined in domain layer, mapped at infrastructure layer

## �🚀 Quick Start

### Prerequisites

- Docker & Docker Compose
- Java 21 (if running locally without Docker)
- Node.js 20+ (if running frontend locally)
- Maven 3.9+ (if running backend locally)

### Run with Docker Compose

The easiest way to run the entire application:

```bash
# Start all services (MySQL, Backend, Frontend)
docker-compose up -d

# View logs
docker-compose logs -f

# Stop all services
docker-compose down

# Stop and remove all data
docker-compose down -v
```

**Access the application:**
- Frontend: http://localhost:3001
- Backend API: http://localhost:8080/api
- Swagger UI: http://localhost:8080/swagger-ui.html
- API Docs: http://localhost:8080/api-docs

### Run Backend Locally

```bash
cd server

# Start MySQL (required)
docker run -d \
  --name ezycollect-mysql \
  -e MYSQL_ROOT_PASSWORD=root_password \
  -e MYSQL_DATABASE=ezycollect \
  -e MYSQL_USER=ezycollect_user \
  -e MYSQL_PASSWORD=ezycollect_password \
  -p 3307:3306 \
  mysql:8.0

# Build and run the backend
./mvnw clean install
./mvnw spring-boot:run

# Or with custom encryption key
ENCRYPTION_SECRET_KEY=MyCustomKey16 ./mvnw spring-boot:run
```

### Run Frontend Locally

```bash
cd client

# Install dependencies
npm install

# Copy environment file
cp .env.local.example .env.local

# Run development server
npm run dev

# Build for production
npm run build
npm start
```

## 📡 API Examples

### Create a Payment

```bash
curl -X POST http://localhost:8080/api/payments \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "John",
    "lastName": "Doe",
    "zipCode": "12345",
    "cardNumber": "4532015112830366",
    "amount": 100.00
  }'
```

**Response:**
```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "firstName": "John",
  "lastName": "Doe",
  "zipCode": "12345",
  "maskedCardNumber": "************0366",
  "amount": 100.00,
  "status": "PROCESSED",
  "createdAt": "2026-02-13T10:30:00Z"
}
```

### Register a Webhook

```bash
curl -X POST http://localhost:8080/api/webhooks \
  -H "Content-Type: application/json" \
  -d '{
    "endpointUrl": "https://webhook.site/your-unique-url"
  }'
```

**Response:**
```json
{
  "id": "7c9e6679-7425-40de-944b-e07fc1f90ae7",
  "endpointUrl": "https://webhook.site/your-unique-url",
  "active": true,
  "createdAt": "2026-02-13T10:30:00Z",
  "updatedAt": "2026-02-13T10:30:00Z"
}
```

### Webhook Payload Example

When a payment is created, webhooks receive a POST request:

```json
{
  "paymentId": "550e8400-e29b-41d4-a716-446655440000",
  "firstName": "John",
  "lastName": "Doe",
  "zipCode": "12345",
  "amount": 100.00,
  "status": "PROCESSED",
  "createdAt": "2026-02-13T10:30:00Z",
  "eventType": "payment.created"
}
```

## 🧪 Testing Webhooks

Use [webhook.site](https://webhook.site) to test webhook notifications:

1. Go to https://webhook.site
2. Copy your unique URL
3. Register it as a webhook in the application
4. Create a payment
5. See the webhook notification in webhook.site

## 🔒 Security

### Encryption

- Card numbers are encrypted using **AES-128** encryption
- Encryption key is configurable via environment variable
- **Production warning**: Always use a strong, unique encryption key in production

### Environment Variables

```bash
# Backend
ENCRYPTION_SECRET_KEY=YourSecureKey16Chars  # Must be 16 chars for AES-128
SPRING_DATASOURCE_URL=jdbc:mysql://localhost:3306/ezycollect
SPRING_DATASOURCE_USERNAME=ezycollect_user
SPRING_DATASOURCE_PASSWORD=ezycollect_password

# Frontend
NEXT_PUBLIC_API_URL=http://localhost:8080/api
```

## 📊 Database Schema

### Payments Table
```sql
CREATE TABLE payments (
    id CHAR(36) PRIMARY KEY,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    zip_code VARCHAR(20) NOT NULL,
    encrypted_card_number VARCHAR(500) NOT NULL,
    amount DECIMAL(10,2) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
```

### Webhooks Table
```sql
CREATE TABLE webhooks (
    id CHAR(36) PRIMARY KEY,
    endpoint_url VARCHAR(500) NOT NULL UNIQUE,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
```

## 🔧 Configuration

### Backend Configuration

See [application.properties](server/src/main/resources/application.properties) for full configuration options:

- Database connection settings
- JPA/Hibernate configuration
- Logging levels
- OpenAPI documentation
- CORS settings
- Async execution settings

### Frontend Configuration

See [next.config.js](client/next.config.js) for Next.js configuration.

## 📝 API Documentation

Full OpenAPI specification is available at:
- **File**: [openapi.yaml](openapi.yaml)
- **Live Swagger UI**: http://localhost:8080/swagger-ui.html
- **JSON Format**: http://localhost:8080/api-docs

## 🛠️ Development

### Backend Development

```bash
cd server

# Run tests
./mvnw test

# Package application
./mvnw package

# Build Docker image
docker build -t ezycollect-backend .
```

### Frontend Development

```bash
cd client

# Run linter
npm run lint

# Type check
npm run type-check

# Build for production
npm run build
```

## 🐳 Docker Commands

```bash
# Build all images
docker-compose build

# Start specific service
docker-compose up backend

# View service logs
docker-compose logs -f backend

# Restart service
docker-compose restart backend

# Execute command in container
docker-compose exec backend sh

# Scale services (if needed)
docker-compose up -d --scale backend=2
```

## 📚 Tech Stack

### Backend
- **Java 21** - Programming language
- **Spring Boot 4.0.2** - Application framework
- **Spring Data JPA** - Data access
- **MySQL 8.0** - Database
- **Hibernate** - ORM
- **Spring Retry** - Resilience
- **SpringDoc OpenAPI** - API documentation
- **WebFlux** - Reactive HTTP client for webhooks

### Frontend
- **React 19** - UI library
- **Next.js 15** - React framework
- **TypeScript** - Type safety
- **Axios** - HTTP client

### DevOps
- **Docker** - Containerization
- **Docker Compose** - Multi-container orchestration
- **Maven** - Build tool

## 🎯 Project Structure

```
ezy-collect/
├── server/                 # Backend (Java + Spring Boot)
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/
│   │   │   │   └── com/ezycollect/server/
│   │   │   │       ├── domain/
│   │   │   │       ├── application/
│   │   │   │       ├── infrastructure/
│   │   │   │       └── entrypoint/
│   │   │   └── resources/
│   │   │       ├── application.properties
│   │   │       └── db/init.sql
│   │   └── test/
│   ├── Dockerfile
│   └── pom.xml
│
├── client/                # Frontend (React + Next.js)
│   ├── src/
│   │   ├── app/
│   │   │   ├── page.tsx
│   │   │   ├── layout.tsx
│   │   │   └── globals.css
│   │   ├── components/
│   │   │   ├── payments/
│   │   │   │   ├── PaymentForm.tsx
│   │   │   │   ├── PaymentCard.tsx
│   │   │   │   ├── PaymentsListPage.tsx
│   │   │   │   ├── SearchFilter.tsx
│   │   │   │   └── Pagination.tsx
│   │   │   └── webhooks/
│   │   │       └── WebhookManager.tsx
│   │   └── lib/
│   │       └── api.ts
│   ├── Dockerfile
│   ├── package.json
│   └── tsconfig.json
│
├── docker-compose.yml     # Docker Compose configuration
├── openapi.yaml          # OpenAPI specification
└── README.md             # This file
```

## 🤝 Contributing

This project follows:
- **Clean Code** principles
- **SOLID** principles
- **Hexagonal Architecture**
- **Test-Driven Development** (TDD)

## 📄 License

Apache 2.0

## 👥 Team

Douglas de Barros Silva - dbs.douglas@gmail.com

---

Made using Clean Code and Hexagonal Architecture
