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
All errors return a consistent JSON response structure:
{
"status": 404,
"error": "Not Found",
"message": "Asset id 42 not found",
"path": "/assets/42",
"timestamp": "2025-10-17T14:32:10Z"
}
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

### Running Locally
1. Clone the repository  
   git clone https://github.com/your-username/portfolio-tracker.git  
   cd portfolio-tracker

2. Start PostgreSQL with Docker  
   docker-compose up -d

3. Run the application  
   ./mvnw spring-boot:run

   The API will be available at: http://localhost:8080  
   Swagger/OpenAPI docs: http://localhost:8080/swagger-ui.html
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
## Roadmap

The current system provides a solid foundation for tracking assets, transactions, and portfolio holdings.  
Planned future enhancements include:

- **Production-ready financial API integration**  
  Replace the demo Alpha Vantage API with a high-availability data provider (e.g., IEX Cloud, Polygon.io) including retry policies, rate-limit handling, and caching.

- **User authentication & multi-tenancy**  
  Support multiple users with secure login and per-user portfolio isolation.

- **Portfolio analytics dashboard**  
  Add endpoints (and a basic UI if extended) for performance charts, asset allocation breakdown, and risk metrics.

- **Extended asset classes**  
  Support options, mutual funds, and international securities alongside equities, ETFs, crypto, and cash.

- **Deployment & scalability**  
  Package the app with Docker, define CI/CD pipelines, and prepare for cloud deployment (e.g., Kubernetes, AWS/GCP).

- **Monitoring & observability**  
  Add metrics, logging, and health checks with Spring Boot Actuator and Prometheus/Grafana integration.
---
## License
MIT License © 2025 Or Davidovitz