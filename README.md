# RuneStone Bank 

RuneStone Bank is a full-stack modern banking application featuring a React-based frontend and a Spring Boot (Java/Kotlin) backend. The project follows a monorepo structure designed for scalability and clear separation of concerns.

##  Project Architecture

The project is organized into three main modules:

- **[`server/`](./server)**: The core backend service built with Spring Boot. Features a hybrid development model as it transitions from Java to Kotlin. 
- **[`client/`](./client)**: A modern, responsive web dashboard built with React, TypeScript, and Tailwind CSS.
- **[`infra/`](./infra)**: Infrastructure-as-Code and deployment configurations (Terraform, Docker).

---

##  Tech Stack

### Backend (`server/`)
- **Framework:** Spring Boot 3.5.7
- **Languages:** Java 21 & Kotlin 2.1.0 (Mixed-Source)
- **Database:** PostgreSQL (with Spring Data JPA)
- **Security:** Spring Security & JWT Authentication
- **Documentation:** SpringDoc OpenAPI (Swagger)

### Frontend (`client/`)
- **Framework:** React 18
- **Build Tool:** Vite
- **Styling:** Tailwind CSS
- **Type Safety:** TypeScript
- **State Management:** React Hooks & Context API

---

## Getting Started

### Prerequisites
- JDK 21+
- Node.js 18+
- Docker & Docker Compose (for PostgreSQL)

### Running the Backend
```bash
cd server
./mvnw spring-boot:run
```
*Note: Make sure to configure your `.env` file in the `server` directory for environment variables like `JWT_SECRET`.*

### Running the Frontend
```bash
cd client
npm install
npm run dev
```

---

# Current improvements
## 📖Kotlin Migration

This project is currently undergoing a strategic migration of non-core domains from Java to Kotlin to leverage its modern syntax and null-safety features. 
- The `EmailService` and associated listeners have been successfully migrated to Kotlin.
- Mixed compilation is supported via the `kotlin-maven-plugin`.

---

