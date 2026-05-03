# API Gateway Service

The **API Gateway** is the single entry point for the OmniRecharge application. It handles routing, security, and cross-origin resource sharing (CORS) for all microservices.

## ⚙️ Core Configuration

- **Port**: `8080`
- **Technology**: **Spring Cloud Gateway**
- **Security**: **JWT (JSON Web Token)** verification via `JwtAuthenticationFilter`.

## 🛠️ Key Features

- **Dynamic Routing**: Automatically routes traffic to services registered in Eureka (e.g., `/api/users/**` -> `user-service`).
- **Security Filter**: Extracts the JWT from the `Authorization` header, validates the signature, and populates the `X-User-Role` and `X-User-Id` headers for downstream services.
- **Global CORS**: Centralized CORS configuration to allow the Angular frontend (`port 4200`) to communicate with all backend APIs.
- **Resilience**: Protects internal microservices from being directly exposed to the public internet.

## 🔒 Security Logic
The gateway checks for valid JWTs on all routes except:
- `/api/users/login`
- `/api/users/register`
- `/api/operators` (public listing)

---
*Developed by Paila Murali Madhav*
