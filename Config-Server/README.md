# Spring Cloud Config Server

The **Config Server** provides a centralized way to manage external properties for all microservices across all environments. It ensures that configuration is kept separate from the code.

## ⚙️ Core Configuration

- **Port**: `8888`
- **Annotation**: `@EnableConfigServer`
- **Storage**: Currently configured to use **Native File System** storage (located in `src/main/resources/config`).

## 🛠️ Key Features

- **Environment Isolation**: Supports different profiles (`dev`, `prod`, `docker`).
- **Dynamic Updates**: Configuration can be updated without restarting services (using `/actuator/refresh`).
- **Standardized Properties**: Centralizes common settings like RabbitMQ URLs, Eureka URLs, and MySQL credentials.

## 📂 Configuration Mapping
The server serves files based on the service name:
- `user-service.properties`
- `operator-service.properties`
- `recharge-service.properties`
- etc.

---
*Developed by Paila Murali Madhav*
