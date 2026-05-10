# Enterprise ITSM Backend: Final Demo Runbook

This runbook outlines the step-by-step presentation script to demonstrate the Toyota ITSM Backend as a complete, enterprise-grade system. This flow proves the system handles complex enterprise lifecycles, rigorous security, optimized reporting, and modular workflows beyond basic CRUD operations.

**Note:** The demo can be executed using **Swagger UI** (`http://localhost:8080/swagger-ui.html`) or **Postman**.

---

## 1. Start Infrastructure
**Objective:** Prove the system relies on robust containerized dependencies.

1. Verify Docker environment is running:
   ```bash
   docker ps
   ```
2. Ensure **Keycloak** (IAM) and **PostgreSQL** are running (typically via `docker-compose up -d`).
3. Boot the Spring Boot Backend:
   ```bash
   ./mvnw spring-boot:run
   ```
4. Open the fully polished **Swagger/OpenAPI UI** in the browser to visualize the API structure.

---

## 2. Authentication
**Objective:** Demonstrate enterprise IAM via JWT and Role-Based Access Control.

1. Request JWT Access Tokens from Keycloak (or via Postman OAuth2 integration).
2. Explain the available Demo Personas:
    - **ADMIN**: Global system oversight, reporting, and security.
    - **MANAGER**: Operational queue management and SLA configuration.
    - **AGENT**: Technical owner resolving tickets.
    - **CUSTOMER**: End-user logging incidents.

---

## 3. Customer Flow
**Objective:** End-user interaction and ticket initiation.

1. Authenticate as **CUSTOMER**.
2. **Create Ticket** (`POST /api/tickets`):
    - Submit an issue (e.g., "VPN Connection Failing").
    - Notice the status immediately defaults to `NEW`.
    - The backend automatically extracts `requesterId` from the JWT token (Security by design).
3. **View My Tickets** (`GET /api/tickets/my`):
    - Show that the Customer can only see tickets they own.

---

## 4. Manager/Admin Flow
**Objective:** Queue governance and dispatching.

1. Authenticate as **MANAGER** or **ADMIN**.
2. **Assign Ticket** (`PUT /api/tickets/{id}/assign`):
    - Provide the Agent's ID.
    - **Highlight:** Highlight that assignment operations integrate directly with the workflow engine, moving the ticket securely into the operational assignment lifecycle while logging each state in the workflow history.

---

## 5. Agent Flow
**Objective:** Technical resolution lifecycle.

1. Authenticate as **AGENT**.
2. **Claim Ticket** (`PUT /api/tickets/{id}/claim`):
    - Show how an Agent can voluntarily pull an unassigned ticket from a queue.
3. **Acknowledge Work** (`PUT /api/tickets/{id}/status`):
    - Transition ticket from `ASSIGNED` to `IN_PROGRESS`.
4. **Collaboration** (`POST /api/tickets/{id}/comments`):
    - Add a standard comment (visible to the customer).
    - Add an **internal comment** (`isInternal = true`). Highlight that the Customer API endpoints filter this out.
5. **Attachments** (`POST /api/tickets/{id}/attachments`):
    - Upload a diagnostic log file or screenshot via multipart form-data.
6. **Effort Tracking** (`POST /api/tickets/{id}/worklogs`):
    - Log 45 minutes of work. This feeds directly into reporting performance metrics.

---

## 6. SLA Flow
**Objective:** Showcase automated governance.

1. **View SLA Tracking** (`GET /api/sla/tickets/{id}`):
    - Show the dynamic deadlines (First Response Due, Resolution Due).
2. **Pause/Resume Behavior**:
    - Explain that if the Agent transitions the ticket to `WAITING_FOR_CUSTOMER`, the SLA timer is cleanly paused, and resumes upon the next response.
3. **Escalation Engine**:
    - Explain the asynchronous Spring `@Scheduled` background job. If an SLA breaches, it flags the ticket, creates an `SLA_ESCALATED` workflow event, and triggers urgent notifications to Management.

---

## 7. Resolution Flow
**Objective:** Validated state transitions.

1. **Resolve Ticket** (`PUT /api/tickets/{id}/status` -> `RESOLVED`):
    - **Show Validation Error:** Attempt to resolve without providing a resolution note. The backend throws an immediate `BadRequestException` rejecting the transition.
    - Provide the required note and succeed.
2. **View History** (`GET /api/tickets/{id}/workflow-history`):
    - Show the granular, unalterable trail of the ticket's lifecycle.

---

## 8. Reopen / Close Flow
**Objective:** Lifecycle termination and exception handling.

1. **Reopen** (`PUT /api/tickets/{id}/reopen`):
    - Authenticate as **CUSTOMER**.
    - State that the issue persists. The ticket shifts from `RESOLVED` back to `IN_PROGRESS`.
2. **Close** (`PUT /api/tickets/{id}/close`):
    - Authenticate as **MANAGER** or **CUSTOMER** and gracefully terminate the ticket (`CLOSED`).
3. **Auto-Close Job**:
    - Explain the backend governance job that sweeps stale `RESOLVED` tickets (e.g., inactive for 3 days) and automatically closes them, maintaining queue hygiene.

---

## 9. Reporting Flow
**Objective:** Performant analytical aggregates for leadership.

1. Authenticate as **ADMIN** or **MANAGER**.
2. **Ticket Summary** (`GET /api/reports/tickets/summary`):
    - Highlight the single-pass database query (no N+1).
3. **SLA Compliance** (`GET /api/reports/sla/compliance?days=30`):
    - Show metric calculations over the requested 30-day window.
4. **Agent Performance** (`GET /api/reports/agents/performance`):
    - Show aggregated assigned/resolved counts and total worklog minutes. Mention this leverages optimized JPQL projections to prevent memory exhaustion.

---

## 10. Audit / History Flow
**Objective:** Enterprise Compliance.

1. **Audit Logs** (`GET /api/audit`):
    - Show system-level audit trails capturing entity creations, updates, and deletes, critical for compliance.

---

## 11. Final Talking Points (Architecture Review)

Wrap up the demo by highlighting the deep architectural foundations:

1. **Package-by-Feature Design:** The codebase is modular (SLA, Workflow, Reporting, Ticket, Auth), highly readable, and domain-focused.
2. **Keycloak JWT RBAC:** Hardened, stateless security trusting an external IAM.
3. **Resource-Level Security:** A Manager can see all, but a Customer only accesses their own resources safely.
4. **Flyway Migrations:** Version-controlled database schema providing strict deployment discipline.
5. **Dockerized Infrastructure:** Portable, "run-anywhere" ecosystem.
6. **Optimized Queries & Indexes:** Projections, aggregation grouping, and B-Tree indexing ensure the backend will not collapse under heavy data volumes.
7. **Future-Ready Workflow Abstraction:** The state transitions are localized behind a `WorkflowService` interface, purposefully built to seamlessly plug into Red Hat jBPM for advanced business process execution.
