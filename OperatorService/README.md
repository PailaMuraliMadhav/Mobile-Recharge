# Operator Service

The **Operator Service** is the catalog manager for the platform. It handles the inventory of telecom operators and their associated recharge plans.

## ⚙️ Core Configuration

- **Port**: `8082`
- **Database**: `recharge_operator_db` (MySQL)
- **Persistence**: Spring Data JPA

## 🛠️ Key Features

- **Operator Management**: CRUD operations for telecom operators (Airtel, Jio, VI, BSNL, etc.).
- **Logo Integration**: Stores and serves logo URLs for visual branding in the frontend.
- **Plan Management**: Dynamic management of plans, including pricing, validity, data benefits, and descriptions.
- **Filtering**: Allows fetching plans filtered by operator and activity status.

## 📡 API Endpoints

| Method | Endpoint | Description |
| :--- | :--- | :--- |
| `GET` | `/api/operators` | Get all active operators |
| `POST` | `/api/operators` | Add new operator (Admin) |
| `GET` | `/api/operators/{id}/plans` | Get all plans for an operator |
| `POST` | `/api/operators/{id}/plans` | Add a new plan (Admin) |
| `DELETE` | `/api/operators/plans/{id}` | Remove a plan (Admin) |

---
*Developed by Paila Murali Madhav*
