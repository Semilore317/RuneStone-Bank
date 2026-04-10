# RuneStone Bank

RuneStone Bank is a sophisticated **Modular Monolith** banking system engineered for transactional integrity and decoupled domain boundaries. Moving beyond simple CRUD operations, this system tackles the complexities of distributed messaging, atomic state changes, and polyglot service boundaries.

---

## 1. Architectural Blueprint
![diagram](https://github.com/user-attachments/assets/d3c872b6-7271-439b-9caa-0095326080fc)


---

## 2. Engineering Highlights

| Challenge | RuneStone Solution |
| :--- | :--- |
| **System Decoupling** | Inter-module communication is fully decoupled using **Apache Kafka** (Events: `user.registered`, `transaction.completed`, `statement.ready`). The core transaction domain never waits on the notification domain. |
| **Data Consistency** | Implemented the **Transactional Outbox Pattern** to solve the dual-write problem. Database commits and event capturing happen atomically before being routed to brokers. |
| **Security & Auth** | Stateless **JWT Authentication** with custom Spring Security filter chains and strict method-level `@PreAuthorize` authorization for resource ownership. |
| **Polyglot Design** | Unified **Java + Kotlin** compilation pipeline. Core banking ledger in Java; side-effect listeners (Email Module) in Kotlin. |

---

## 3. Deep Dives into Technical Decisions

### Event-Driven Messaging & The Outbox Pattern
**Why not just call an email service synchronously?**
If the SMTP server goes down, a user shouldn't fail to transfer funds. 
To decouple the Ledger module from the Notification module, RuneStone uses an event-driven approach. To prevent the "dual-write" problem (committing to the DB but failing to publish to Kafka), i implemented the **Transactional Outbox pattern**. Domain events (e.g., `TransactionCompletedEvent`) are saved to an `Outbox` table in the exact same transaction as the ledger update. A CDC connector (Debezium) streams these outbox entries to Kafka topics, ensuring high availability and guaranteed delivery.

### Concurrency and Transactional Integrity
Handling money requires strict isolation. Funds movement is wrapped in boundaries using Spring's `@Transactional`, ensuring that both debit and credit operations in a transfer succeed or fail together.

### CI/CD and GraalVM Native Images
RuneStone leverages **GitHub Actions** for an automated CI/CD pipeline. To minimize cold starts and resource footprint in production, the Maven pipeline uses **GraalVM** to compile a Native Image (`Dockerfile.prod`). Upon a successful build, the workflow directly deploys the updated container to an Azure Virtual Machine.

---

## 4. API Documentation & Observability

- **Swagger/OpenAPI UI:** Fully documented interactive API available at `http://localhost:8081/swagger-ui/index.html`.
- **System Health:** Actuator endpoints (`/actuator/health`, `/actuator/info`) exposed for monitoring and readiness probes.

---

## 5. What's Next?
- **Comprehensive Testing Suite:** Integration testing with **Testcontainers** to spin up an ephemeral PostgreSQL and Kafka instance, targeting >80% coverage.
- **Concurrency Hardening:** Explicit Optimistic Concurrency Control (`@Version`) strategies targeted for high-contention accounts.
- **Gradle Migration:** Moving from Maven to Gradle for faster, incremental polyglot builds and better dependency management caching.
- **Intelligence Layer:** 
  - *The Legal Counsel (Java + Drools):* Deterministic validation gateway.
  - *The Evidence Room (Neo4j):* Structural graph memory for tracking money movements.
  - *The Detective (Kotlin + Koog + Spring AI):* Agentic reasoning layers for potential fraud detection.

---

# Getting Started
## 1. Monorepo Structure

- `server/` - Spring Boot backend (Java + Kotlin)
- `client/` - React frontend (Vite, TypeScript, Tailwind)
- `infra/` - Container and deployment assets

---

## 2. Local Development Guide

### Prerequisites
- JDK 21+
- Node.js 18+
- Docker + Docker Compose

### i) Backend Launch
```bash
cd server
./mvnw spring-boot:run
```
Backend default URL: `http://localhost:8081`

### ii) Frontend Launch
```bash
cd client
npm install
npm run dev
```
Frontend default URL: `http://localhost:5173`

### iii) Infrastructure (Database) Launch
```bash
cd infra
docker compose up -d
```

### iv) Verify Runtime Health
```bash
curl http://localhost:8081/actuator/health
```
