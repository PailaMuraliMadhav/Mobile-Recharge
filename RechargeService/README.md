# Recharge Service

The **Recharge Service** is the central orchestrator of the OmniRecharge platform. It coordinates between users, operators, and payments to process mobile recharges.

## ⚙️ Core Configuration

- **Port**: `8083`
- **Database**: `recharge_main_db` (MySQL)
- **Messaging**: **RabbitMQ** (Exchange: `recharge.exchange`, Queue: `recharge.success.queue`)
- **Inter-Service**: **OpenFeign** clients for `User`, `Operator`, and `Payment` services.

## 🛠️ Key Features

- **Transaction Orchestration**: Manages the complete lifecycle of a recharge, from initiation to success/failure.
- **Synchronous Integration**: Uses `@FeignClient` to verify user existence and plan validity in real-time.
- **Asynchronous Notifications**: Upon successful recharge, it publishes an event to RabbitMQ for the **Notification Service** to handle.
- **Transaction History**: Provides a consolidated view of all recharges for both users and admins.

## 📡 Connections & Annotations

- **`@FeignClient`**: Used to define declarative REST clients for communicating with other microservices.
- **`@RabbitListener`**: (In Notification) Listens to messages published by this service.
- **`RabbitTemplate`**: Used to publish success events.

## 📡 API Endpoints

| Method | Endpoint | Description |
| :--- | :--- | :--- |
| `POST` | `/api/recharges/process` | Main entry point for a new recharge |
| `GET` | `/api/recharges/user/{id}` | Get recharge history for a user |
| `GET` | `/api/recharges/all` | Get all platform recharges (Admin) |

---
*Developed by Paila Murali Madhav*
