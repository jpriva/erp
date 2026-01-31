# ERP Backend

Spring Boot backend for the ERP application using Domain-Driven Design and Hexagonal Architecture.

## Tech Stack

- **Java 25** + **Spring Boot 4.0.2**
- **Spring Modulith** - Modular monolith architecture
- **PostgreSQL 17** + **Flyway** - Database and migrations
- **Testcontainers** - Integration testing
- **Lombok** - Code generation

## Quick Start

```bash
# 1. Start database
cp .env.example .env
docker compose up -d

# 2. Run application
./gradlew bootRun
```

## Commands

```bash
./gradlew build              # Full build with tests
./gradlew test               # Run all tests
./gradlew testWithLog        # Run tests with console output
./gradlew bootRun            # Start application

# Run specific tests
./gradlew test --tests "*UserTest"
./gradlew test --tests "*RoleTest.create_Success"
```

## Project Structure

```
src/main/java/com/jpriva/erpsp/
├── auth/                           # Auth module (Spring Modulith boundary)
│   ├── domain/
│   │   ├── model/                  # Aggregates and Value Objects
│   │   │   ├── user/               # User, UserId, UserStatus
│   │   │   ├── credential/         # PasswordCredential, OpenIdCredential
│   │   │   ├── tenant/             # Tenant, TenantId, TenantName
│   │   │   ├── role/               # Role, RoleId, RoleName
│   │   │   ├── membership/         # TenantMembership
│   │   │   └── invitation/         # Invitation
│   │   ├── ports/
│   │   │   ├── in/                 # Use Cases (input ports)
│   │   │   └── out/                # Repository & infra interfaces (output ports)
│   │   └── constants/              # ErrorCode and ValidationErrorCode enums
│   ├── application/
│   │   └── usecases/               # Use case implementations
│   └── infra/
│       └── out/
│           ├── persistence/        # JPA adapters, entities, mappers
│           └── logger/             # LoggerPort adapter
├── shared/                         # Shared domain layer
│   └── domain/
│       ├── model/                  # Email, PersonName, ValidationError
│       ├── exceptions/             # ErpException hierarchy
│       └── utils/                  # ValidationErrorUtils
└── config/                         # Spring configuration
```

## Architecture Principles

### Hexagonal Architecture

Domain stays pure - all infrastructure accessed via ports:

| Port | Purpose | Adapter |
|------|---------|---------|
| `LoggerPort` | Logging without Slf4j in domain | `Slf4jLoggerAdapter` |
| `TransactionalPort` | Transactions without @Transactional | Spring TX adapter |
| `*RepositoryPort` | Data access | JPA adapters |

### Validation System

Typed errors with i18n codes:

```java
// Define errors in enum
public enum UserValidationError implements ValidationErrorCode {
    EMAIL_EMPTY("email", "validation.auth.user.email.empty", "Email cannot be empty");
}

// Accumulate in constructors
var val = ValidationError.builder();
if (email == null) val.addError(UserValidationError.EMAIL_EMPTY);
ValidationErrorUtils.validate(AuthErrorCode.AUTH_MODULE, val);
```

### Exception Hierarchy

```
ErpException (module, ErrorCode, args)
├── ErpValidationException (400)
├── ErpPersistenceCompromisedException (500)
└── ErpImplementationException (500)
```

## Dependency Management

All dependencies managed in `gradle/libs.versions.toml`. Do not add dependencies directly to `build.gradle`.
