# PayNow - Project Implementation

This project has been implemented with the following structure:

## ✅ Structure

1. **Java 21 + Spring Boot Backend**
   - Configured Maven with Java 21
   - Added all required dependencies (JPA, Web, Validation, OpenAPI, etc.)

2. **Hexagonal Architecture Implementation**
   - Domain Layer: Pure business logic (models, ports)
   - Application Layer: Use cases, DTOs, mappers
   - Infrastructure Layer: JPA adapters, encryption, webhooks
   - Entrypoint Layer: REST controllers

3. **Payment System**
   - Create, read, and list payments
   - Payment amount field with BigDecimal precision (10 digits, 2 decimal places)
   - Amount validation (minimum $0.01, not null)
   - Payment gateway integration (port and adapter pattern)
   - SimplePaymentGateway: always succeeds except when zipCode = "11111"
   - Fraud detection logic (firstName="aaa" && lastName="aaa")
   - AES encryption for card numbers
   - Card number masking in responses
   - Payment status management

4. **Webhook System**
   - Dynamic webhook registration
   - Webhook activation/deactivation
   - Resilient webhook notifications with retry (3 attempts, exponential backoff)
   - Async webhook processing

5. **Database**
   - MySQL schema initialization
   - JPA entities and repositories
   - Database migrations support

6. **Docker & Docker Compose**
   - Multi-stage Dockerfile for backend
   - Multi-stage Dockerfile for frontend
   - Complete docker-compose.yml with MySQL, backend, and frontend
   - Health checks and dependencies

7. **Next.js Frontend**
   - Modular component architecture:
     - `PaymentForm` component for payment creation
     - `WebhookManager` component for webhook management
     - `PaymentCard` component for payment display
     - `PaymentsListPage` component with pagination and search
   - Payment creation form with amount input
   - API integration with Axios
   - Responsive design
   - Form validation with error handling

8. **API Documentation**
   - Complete OpenAPI 3.0 specification
   - Examples for all endpoints
   - Swagger UI integration
   - API documentation at /api-docs

## 🚀 How to Run

```bash
# Using Docker Compose (recommended)
docker-compose up -d

# Access the application
# Frontend: http://localhost:3001
# Backend API: http://localhost:8080/api
# Swagger UI: http://localhost:8080/swagger-ui.html
```

## 📁 Project Structure

```
ezy-collect/
├── server/                         # Spring Boot Backend (Java 21)
│   ├── src/main/java/com/ezycollect/server/
│   │   ├── domain/                # Domain layer
│   │   │   ├── model/            # Payment, Webhook entities
│   │   │   ├── repository/       # Repository ports
│   │   │   ├── service/          # Service ports
│   │   │   └── exception/        # Domain exceptions hierarchy
│   │   ├── application/          # Application layer
│   │   │   ├── usecase/         # PaymentUseCase, WebhookUseCase
│   │   │   ├── dto/             # Request/Response DTOs
│   │   │   └── mapper/          # Domain-DTO mappers
│   │   ├── infrastructure/       # Infrastructure layer
│   │   │   ├── persistence/     # JPA entities and adapters
│   │   │   ├── encryption/      # AES encryption service
│   │   │   ├── gateway/         # Payment gateway implementation
│   │   │   ├── webhook/         # Resilient webhook service
│   │   │   └── config/          # Spring configurations
│   │   └── entrypoint/          # Entrypoint layer
│   │       └── rest/            # REST controllers
│   ├── Dockerfile
│   └── pom.xml
├── client/                        # Next.js Frontend
│   ├── src/
│   │   ├── app/                 # Next.js pages
│   │   ├── components/          # React components
│   │   │   ├── payments/       # PaymentForm, PaymentCard, etc.
│   │   │   └── webhooks/       # WebhookManager
│   │   └── lib/                 # API client
│   ├── Dockerfile
│   └── package.json
├── docker-compose.yml            # Docker orchestration
├── openapi.yaml                  # OpenAPI specification
└── README.md                     # Documentation
```

## 🎯 Key Features Implemented

### Clean Code
- Meaningful names throughout the codebase
- Small, focused methods and classes
- No God classes or static business logic
- Constructor injection only

### SOLID Principles
- **S**: Each class has single responsibility
- **O**: Extend via abstractions (ports/adapters)
- **L**: Implementations respect contracts
- **I**: Focused interfaces (repository, service ports)
- **D**: Dependencies on abstractions, not implementations

### Hexagonal Architecture
- Domain is framework-agnostic
- Clear port definitions (interfaces)
- Adapter implementations (JPA, encryption, webhooks)
- No domain dependencies on infrastructure

### Security
- Card number encryption using AES
- Environment-based configuration
- No hardcoded secrets
- Masked card numbers in responses

### Resilience
- Webhook retry with exponential backoff
- Async webhook processing
- Error handling and logging
- Timeout configuration

## 🔧 Configuration

### Environment Variables

**Backend:**
- `ENCRYPTION_SECRET_KEY`: AES encryption key (16 chars)
- `SPRING_DATASOURCE_URL`: MySQL connection string
- `SPRING_DATASOURCE_USERNAME`: Database username
- `SPRING_DATASOURCE_PASSWORD`: Database password

**Frontend:**
- `NEXT_PUBLIC_API_URL`: Backend API URL

## 📚 API Endpoints

### Payments
- `POST /api/payments` - Create payment
- `GET /api/payments` - List all payments
- `GET /api/payments/{id}` - Get payment by ID

### Webhooks
- `POST /api/webhooks` - Register webhook
- `GET /api/webhooks` - List all webhooks
- `GET /api/webhooks/{id}` - Get webhook by ID
- `DELETE /api/webhooks/{id}` - Delete webhook
- `PATCH /api/webhooks/{id}/activate` - Activate webhook
- `PATCH /api/webhooks/{id}/deactivate` - Deactivate webhook

## ✨ Architecture Highlights

1. **No framework coupling in domain layer**
   - Pure Java POJOs
   - Business rules encapsulated
   - No Spring annotations

2. **Clear separation of concerns**
   - Domain: Business logic
   - Application: Orchestration
   - Infrastructure: Technical details
   - Entrypoint: External interfaces

3. **Testability**
   - 74 passing tests covering all layers
   - Domain can be tested without Spring
   - Use cases can be tested with mocks
   - Integration tests for infrastructure
   - Comprehensive validation tests (amount, card number, etc.)
   - Gateway integration tests (success/failure scenarios)

4. **Extensibility**
   - Easy to add new payment methods
   - Easy to add new notification channels
   - Easy to swap database or encryption

## 🎓 Learning Resources

This implementation demonstrates:
- Hexagonal Architecture (Ports & Adapters)
- Domain-Driven Design (DDD) concepts
- SOLID principles in practice
- Clean Code principles
- Resilience patterns (retry, timeout)
- Async processing
- API design best practices

---
