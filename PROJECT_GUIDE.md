# 🚀 OmniRecharge: The Ultimate Comprehensive Project Guide

Welcome to the **OmniRecharge** project! This guide is designed to help you understand every aspect of the system—from the high-level architecture to the minute details of how data flows through our microservices.

---

## 🏗️ 1. System Architecture

OmniRecharge is built using a **Microservices Architecture**. Instead of one giant application, we've broken the system into smaller, independent services that talk to each other.

### Why Microservices?
- **Scalability**: We can scale individual services (like `RechargeService`) during peak times (like month-ends) without scaling the `UserService`.
- **Resilience**: If the `NotificationService` goes down, users can still recharge; they just won't get an email immediately.
- **Independence**: Different teams can work on different services without stepping on each other's toes.

### Architecture Components
- **Client**: Angular Frontend (Single Page Application).
- **API Gateway**: The entry point for all frontend requests. It handles security and routing.
- **Microservices**: Business logic units (User, Operator, Recharge, Payment, Notification).
- **Messaging**: RabbitMQ for asynchronous communication between services.
- **Infrastructure**: Eureka (Discovery), Config Server (Settings), Zipkin (Tracing).

---

## 🛠️ 2. Technology Stack

| Component | Technology | Why? |
| :--- | :--- | :--- |
| **Backend** | Spring Boot 3.x | Industry standard for Java microservices. |
| **Frontend** | Angular 17+ | Robust framework for complex, enterprise-level UIs. |
| **API Gateway** | Spring Cloud Gateway | Centralized routing, security, and rate limiting. |
| **Discovery** | Netflix Eureka | Allows services to find each other without hardcoding IPs. |
| **Config** | Spring Cloud Config | Centralized configuration for all environments. |
| **Database** | MySQL 8.0 | Reliable relational database for transactions. |
| **Messaging** | RabbitMQ | Handles asynchronous tasks (like emails) to keep the UI fast. |
| **Tracing** | Zipkin | Tracks a request as it hops across multiple services. |
| **Payment** | Simulation | Standard payment gateway integration logic. |
| **Deployment** | Docker & Compose | Ensures the app runs exactly the same on any machine. |

---

## 📂 3. Project Structure: What is Where?

### 📁 Root Directory
- `docker-compose.yml`: The "master script" that starts all 12+ containers (services + DBs + MQ).
- `PROJECT_GUIDE.md`: This file!

### 📁 Microservices (Backend)
1. **`config-server`**: Holds configuration files. Services fetch their settings from here at startup.
2. **`eureka-server`**: The "Phonebook" of our system. Every service registers itself here.
3. **`api-gateway`**: The "Front Door". All frontend requests come here first. It checks your JWT token and routes you to the right service.
4. **`user-service`**: Handles Registration, Login (JWT generation), and Profiles.
5. **`operator-service`**: Manages Telecom Operators (Jio, Airtel, etc.) and their Recharge Plans.
6. **`recharge-service`**: The heart of the app. It handles the logic of a recharge order.
7. **`payment-service`**: Handles the logic for processing and validating payment transactions.
8. **`notification-service`**: Listens to RabbitMQ and sends emails using JavaMailSender.

### 📁 Frontend (`/frontend`)
- `src/app/components`: The visual parts (Dashboard, Recharge, Admin Panel).
- `src/app/services`: Logic to talk to the Backend APIs.
- `src/app/guards`: Prevents regular users from entering Admin pages.
- `src/app/interceptors`: Automatically attaches your JWT token to every request.

---

## 🧱 4. Internal Service Layering (Packaging)

Every microservice in OmniRecharge follows a strict **Layered Architecture**. This keeps the code organized and easy to maintain.

1. **Controller Layer (`@RestController`)**: 
   - **The Entry Point**: Handles incoming HTTP requests.
   - **Responsibility**: Validates input and returns DTOs.
2. **Service Layer (`@Service`)**: 
   - **The Brain**: Contains the actual business logic.
   - **Responsibility**: Coordinates between repositories and other services.
3. **Repository Layer (`@Repository`)**: 
   - **The Librarian**: Handles all database operations via **Spring Data JPA**.
4. **Entity Layer (`@Entity`)**: 
   - **The Blueprints**: Java classes that represent MySQL database tables.
5. **DTO Layer (Data Transfer Objects)**: 
   - **The Courier**: Simple objects used to send and receive data from the API (Security shield).
6. **Exception Layer (`@RestControllerAdvice`)**: 
   - **The Safety Net**: Handles errors globally and returns clean JSON error messages.
7. **Configuration Layer (`@Configuration`)**: 
   - **The Setup**: Defines beans for tools like ModelMapper, RabbitMQ, and Swagger.
8. **Swagger (OpenAPI)**: 
   - **The Documentation**: Automatically generates a web UI (`/swagger-ui.html`) to test APIs.

---

## 🗄️ 5. Database Connectivity & Persistence

OmniRecharge follows the **Database per Service** pattern.

### 1. Schema Isolation
- Each service has its own dedicated MySQL schema:
  - `omni_user`: Profiles and credentials.
  - `omni_operator`: Operators and Plans.
  - `omni_recharge`: History and Orders.
  - `omni_payment`: Transactions.
- **Why?**: If one database has an issue, the others remain unaffected.

### 2. Spring Data JPA
- We use JPA/Hibernate to map Java objects to database tables, avoiding raw SQL.
- **Automatic Updates**: We use `hibernate.ddl-auto: update` to sync tables with Java code automatically.

### 3. Safe Connectivity
- Services connect to MySQL via `jdbc:mysql://host.docker.internal:3306/db_name`.
- **Security**: Database passwords are never hardcoded; they are passed as environment variables in Docker.

---

## 🔑 6. Core Features & Functional Logic

### 🛡️ Role-Based Access Control (RBAC)
- **Users**: Can see their dashboard, recharge, and history.
- **Admins**: Can manage operators, plans, and view system-wide stats like Success Rate.
- **How?**: The JWT token contains the user's role, which is checked by Spring Security.

### ⚡ Suggested Plans Highlighting
- Admins can flag any plan as "Suggested" in the database.
- In the frontend, these plans get a "⭐ RECOMMENDED" badge and a glowing border using CSS classes.

### 👤 Self vs. Friend Recharge
- The system fetches the user's own number from their profile.
- If "Self" is selected, the mobile number field is auto-filled and locked to ensure accuracy.

---

## 🔄 7. The Life of a Recharge: Step-by-Step Flow

When a user clicks "Proceed to Pay", here is exactly what happens:

1. **Frontend**: The `RechargeComponent` sends a request to the API Gateway.
2. **API Gateway**: Validates the JWT token and routes the request to `recharge-service`.
3. **Recharge Service**: 
   - Calls `operator-service` to validate the plan.
   - Saves a record in the database with status `PENDING`.
   - Sends a message to **RabbitMQ** saying "Recharge Initiated".
4. **Notification Service**: Sees the message in RabbitMQ and sends a "Payment Pending" email.
5. **Recharge Service**: Calls `payment-service` to initiate the payment process.
6. **Payment Service**: Creates a new Payment Order and sends details back to the browser.
7. **Browser**: Opens the payment interface where the user enters their details.
8. **After Payment**: The system receives a success signal. `recharge-service` updates the record to `SUCCESS`.
9. **Final Event**: A final "Recharge Successful" email is sent via RabbitMQ.

---

## 📡 8. Inter-Service Communication

### 1. Synchronous Communication (Feign Clients)
- **Use Case**: When one service needs an immediate response from another.
- **Example**: `recharge-service` calling `operator-service` to get plan details:
  ```java
  @FeignClient(name = "OPERATOR-SERVICE")
  public interface OperatorClient {
      @GetMapping("/api/operators/plans/{planId}")
      PlanResponse getPlanById(@PathVariable("planId") Long planId);
  }
  ```

### 2. Asynchronous Communication (RabbitMQ)
- **Use Case**: Background tasks that don't need an immediate response (like emails).
- **Benefit**: Decouples services so that a slow notification service doesn't slow down the recharge.

### 3. Service Discovery (Eureka)
- **How?**: Every service registers with Eureka. Services find each other by name (e.g., `USER-SERVICE`) instead of IP addresses.

---

## 🛡️ 9. Security Implementation

### 🔐 Backend Security (The Fortress)
1. **JWT Sentinel**: The API Gateway intercepts every request and validates the JWT.
2. **RBAC**: The Gateway rejects non-admin users trying to access `/admin/` endpoints.
3. **Password Safety**: We use **BCrypt** hashing so that even if the DB is leaked, passwords are unreadable.
4. **CORS**: Explicitly configured at the Gateway to allow only our trusted frontend.

### 🛡️ Frontend Security (The Shield)
1. **Auth Guards**: `AuthGuard` and `AdminGuard` protect Angular routes.
2. **JWT Interceptor**: Automatically attaches the JWT to every outgoing request.
3. **Conditional Rendering**: Using `*ngIf` to hide administrative buttons from regular users.

---

## 🐳 10. Infrastructure: Docker, RabbitMQ & Zipkin

### 🐳 Docker: The Packaging Expert
- **Why?**: Packages the app and all its settings into a container so it runs exactly the same on any machine.
- **Orchestration**: `docker-compose.yml` conducts all services like an orchestra.

### 🐇 RabbitMQ: The Messenger
- **Why?**: Allows services to send "events" to each other asynchronously, keeping the system fast.

### 🕵️ Zipkin: The Detective
- **Why?**: Provides **Distributed Tracing**. It gives you a visual timeline showing how long a request took in each microservice.

---

## 🛡️ 11. Resilience & Reliability (Circuit Breakers)

- **Problem**: If the Payment Service is down, we don't want the Recharge Service to wait forever and eventually crash.
- **Solution**: **Resilience4j Circuit Breaker** trips the circuit after several failures, returning a "Fallback" message instead of crashing the system.

---

## 🔧 12. Advanced Concepts & Utils

### 1. ModelMapper
- Automatically maps between Entity objects and DTO objects in one line, keeping services clean.

### 2. Stateless Auth
- Since we use JWT and no server-side sessions, we can scale our services horizontally with ease.

### 3. Centralized Configuration
- All services fetch their properties from the **Config Server**, making it easy to manage settings for 8+ services in one place.

---

## 🎤 14. How to Explain This Project (The Presentation Guide)

If you need to explain this project to a teacher, a client, or a friend, follow this structured walkthrough. It’s designed to highlight your technical expertise.

### 1. The "Big Picture" (Start Here)
> "I built **OmniRecharge**, a high-scale mobile recharge platform. Instead of building it as one giant app, I used a **Microservices Architecture**. This means the app is actually a collection of 8 independent services that work together like a well-oiled team."

### 2. Why Microservices? (The "Smart" Reason)
> "By splitting the app into services like *User*, *Payment*, and *Recharge*, the system becomes **Scalable** and **Resilient**. For example, if millions of people are recharging at once, we can scale up just the *Recharge Service* without wasting resources on others. And if the *Notification Service* crashes, the recharges don't stop—the system keeps working."

### 3. The Tech Stack (The "Modern" Reason)
> "I used **Spring Boot 3** for the backend because it's the industry standard for microservices. For the frontend, I chose **Angular 17** for its robust structure and state management. The entire system is **Containerized with Docker**, making it portable and easy to deploy anywhere."

### 4. The Data Flow (The "How it Works" Reason)
> "When a user recharges, it's a multi-step process. The **API Gateway** validates their **JWT token**, the **Recharge Service** checks the plan with the **Operator Service**, and then we use **RabbitMQ** to send emails in the background. This keeps the user experience extremely fast because the UI doesn't have to wait for the email to be sent."

### 5. Advanced Features (The "Wow" Factor)
> "I implemented **Distributed Tracing with Zipkin** to track requests across services, and **Resilience4j Circuit Breakers** to handle service failures gracefully. This isn't just a basic app—it’s designed with enterprise-grade security and reliability patterns."

---

## 🚀 15. How to Run & Use

1. **Build**: Run `mvn clean package` in each microservice folder.
2. **Deploy**: Run `docker-compose up -d --build`.
3. **Access**: 
   - Frontend: [http://localhost:4200](http://localhost:4200)
   - API Docs: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)
   - Eureka: [http://localhost:8761](http://localhost:8761)

---

## 🏁 Conclusion

OmniRecharge is a high-performance, resilient platform designed for the modern web. By using a decoupled microservices architecture, we've ensured that the system is easy to maintain, scale, and extend.
