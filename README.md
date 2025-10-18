# Portfolio Tracker
A backend service for tracking investment portfolios.
It manages assets, holdings, and transactions, while integrating with external price providers.
Built with Java 23, Spring Boot, PostgreSQL, and Docker.
---
## Features
- **Asset management**  
  CRUD operations for assets (equities, ETFs, bonds, crypto, cash, indexes).

- **Transaction tracking**  
  Record buy/sell trades, deposits, and withdrawals with validation and timestamps.

- **Holdings calculation**  
  Automatic aggregation of transactions into current positions and portfolio value.

- **RESTful API design**  
  Clean endpoints with DTOs, validation, and consistent error handling.

- **Pagination & sorting**  
  Paginated queries for assets and transactions with customizable sorting.

- **Database migrations**  
  Managed with Flyway for schema versioning and reproducibility.

- **Integration & unit tests**  
  Coverage for controllers, services, and validation logic to ensure correctness.

- **Error handling**  
  Centralized `GlobalExceptionHandler` for user-friendly API responses.

- **Extensible architecture**  
  Clear separation between controller, service, repository, and DTO layers.
---
## Tech Stack
- Java 23
- Spring Boot 3.5
- Spring Data JPA (Hibernate)
- PostgreSQL (with Docker)
- Flyway for schema migrations
- Spring Validation (Jakarta)
- JUnit 5, MockMvc for testing
- Docker Compose for local setup
- OpenAPI / Swagger documentation
---
## Architecture
- Controllers – Expose REST endpoints, work with DTOs (never expose entities directly).
- Services – Contain business logic, transactional boundaries.
- Repositories – JPA repositories for persistence.
- DTOs – Map input/output models (validation + clean API layer).
- Global Exception Handler – Maps exceptions to standardized JSON error responses.
---
## Error Handling
The API returns structured JSON errors:
```json
{
"status": 404,
"error": "Not Found",
"message": "Asset id 42 not found",
"path": "/assets/42",
"timestamp": "2025-10-17T14:32:10Z"
}
```
### HTTP Status Codes
| Code | When it happens | Example |
|------|----------------------------------|------------------------------------------|
| 200 | Successful GET/PUT/DELETE | `GET /assets/1` returns the resource |
| 201 | Successful creation | `POST /assets` created a new asset |
| 400 | Validation failed / bad payload | Missing `quantity` for `BUY` |
| 404 | Resource not found | Asset/Transaction does not exist |
| 409 | Conflict / duplicate resource | Symbol already exists |
| 500 | Unexpected server error | Unhandled exception (should be rare) |
---
## Testing
Tests include:
- Unit Tests
- AssetServiceUnitTest – validates asset business rules.
- TransactionPayloadValidatorUnitTest – checks transaction validation logic.
- Integration Tests
- AssetControllerSmokeIT – verifies controller–service–repository flow.
- TransactionControllerIT – full flow using PostgreSQL.
  Run tests with:
  ./mvnw test
---
## Installation
### 1) Clone the repository
```bash
git clone https://github.com/OrDavidovitz/portfolio-tracker.git
cd portfolio-tracker
```
### 2) Start PostgreSQL with Docker
```bash
docker-compose up -d
```
### 3) Run the application
```bash
./mvnw spring-boot:run
```
API: http://localhost:8080
Swagger: http://localhost:8080/swagger-ui.html
---
## Profiles
- dev – default profile, connects to local PostgreSQL via Docker.
- test – used for integration tests, runs against a clean schema.
---
## Database
- Migrations: Managed with Flyway in /resources/db/migration.
- Entities:
- Asset
- Transaction
- Holding
- Portfolio
---
## Releases
We use semantic versioning (MAJOR.MINOR.PATCH).
- **v1.0.0** – Core CRUD for assets, transactions, holdings aggregation, validation, Flyway, Docker, Swagger docs.
- **v1.1.0** – Portfolio analytics endpoints (holdings, portfolio value, PnL calculations, aggregated positions) and caching for external price lookups.
- **v1.2.0 (planned)** – User authentication & multi-tenancy.
- **v2.0.0 (planned)** – Production-grade market data provider, observability, Kubernetes deployment
---
## License
MIT License © 2025 Or Davidovitz
