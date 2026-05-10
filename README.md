# Enterprise ITSM Backend

## Overview
A production-style IT Service Management (ITSM) backend focused on workflow orchestration, SLA governance, reporting, and secure operational management.

## Tech Stack
* Java 21
* Spring Boot 3.3.x
* Spring Security
* JWT
* Keycloak
* PostgreSQL
* Flyway
* JPA/Hibernate
* Maven
* Swagger/OpenAPI
* Docker Compose

## Core Features
* Ticket management
* SLA governance
* Workflow abstraction layer
* Attachments
* Worklogs
* Reporting APIs
* Role-based access control
* Audit/history support

## Architecture
Built using a modern, scalable backend architecture prioritizing domain-driven design principles. The backend follows a package-by-feature modular architecture. The system implements a decoupled structure, separating business logic from execution mechanics. It is designed to be fully **BPM-ready** from the ground up.

## Workflow Lifecycle
* Current release uses a custom Java workflow abstraction
* Architecture is BPM-ready
* Future jBPM/BPMN integration is planned

**Current implemented lifecycle:**
`NEW &rarr; IN_PROGRESS &rarr; WAITING_FOR_CUSTOMER &rarr; RESOLVED &rarr; CLOSED`

**Future planned lifecycle:**
`NEW &rarr; TRIAGE &rarr; ASSIGNED &rarr; IN_PROGRESS &rarr; RESOLVED &rarr; CLOSED`

## Security
* **Authentication:** Stateless JSON Web Tokens (JWT)
* **Identity Management:** Keycloak integration
* **Authorization:** Role-Based Access Control (RBAC) on all endpoints

## Local Setup
```bash
git clone <repository-url>
docker-compose up -d
./mvnw spring-boot:run
```

## Swagger/OpenAPI
Interactive API documentation is available at:
`http://localhost:8080/swagger-ui/index.html`

## Environment Variables
Application configuration relies on environment variables. Refer to the `.env.example` file for required credentials and endpoints.

## Documentation
* `workflow-engine-decision.md`
* `schema-quality-checklist.md`
* `release-notes-v1.0.0.md`
* `demo-script.md`
* `flyway-migration-audit.md`

## CI/CD
* GitHub Actions CI
* JaCoCo coverage
* Docker support

## Roadmap
*(Future enhancements only)*
* jBPM/BPMN
* Kafka
* Redis
* OpenSearch
* Kubernetes
* Monitoring
* WebSocket notifications
* Frontend portal

## Version
v1.0.0

## License
This project is intended for educational and portfolio purposes.
