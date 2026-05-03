# 🚀 Docker Quickstart Cheat Sheet

## 🛠 1. How to Start / Stop
Run these from the `d:\Mobile Recharge` folder:

*   **Start Everything**: `docker compose up -d`
*   **Stop Everything**: `docker compose down`
*   **Rebuild (after code changes)**: `docker compose up -d --build --remove-orphans`
*   **Check Logs**: `docker logs -f [service-name]` (e.g., `user-service`)

## 🌐 2. Essential URLs
*   **API Gateway (Main Entry)**: `http://localhost:8080`
*   **Swagger Documentation**: `http://localhost:8080/swagger-ui.html`
*   **Eureka Dashboard**: `http://localhost:8761`
*   **Zipkin Tracing**: `http://localhost:9411`
*   **RabbitMQ Manager**: `http://localhost:15672` (User: `guest`, Pass: `guest`)

## 🔑 3. Implementation Secrets
*   **Database**: Connected to **Local Windows MySQL** (`host.docker.internal:3306`). No data is stored in Docker.
*   **Localhost Fix**: We use `SPRING_APPLICATION_JSON` in `docker-compose.yml` to override the hardcoded `localhost` inside your code.
*   **Git Config**: The Config Server automatically pulls the latest Swagger and Routing rules from your GitHub repo.
*   **Resilience**: Added `restart: on-failure` and `dns: 8.8.8.8` to handle Docker Windows network glitches.

## ⚠️ 4. Troubleshooting
*   **"Port 9411 already in use"**: Another Zipkin is running. Run `netstat -ano | findstr :9411` then `taskkill /F /PID [number]`.
*   **"Connection Refused" (Database)**: Make sure your local MySQL allows connections from `'root'@'%'`.
*   **Stuck on "Created"**: Docker is waiting for the Config Server or Eureka to report "Healthy" before starting business services. Just wait 2 minutes.
