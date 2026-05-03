# Payment Service

The **Payment Service** handles the financial transactions for the OmniRecharge platform. It acts as a secure mock for a real payment gateway (UPI, Cards, Netbanking).

## ⚙️ Core Configuration

- **Port**: `8084`
- **Database**: `recharge_payment_db` (MySQL)
- **Status**: Wallet functionality has been **Decommissioned**.

## 🛠️ Key Features

- **Payment Processing**: Simulates real-world payment gateway responses for different modes (UPI, CREDIT_CARD, DEBIT_CARD, NETBANKING).
- **Transaction Tracking**: Generates unique `transactionId`s for every payment attempt.
- **Audit Log**: Maintains a detailed record of every payment, including its status (SUCCESS, FAILED, PENDING).
- **Security**: Ensures that payment records are immutable and linked to specific recharges.

## 📡 API Endpoints

| Method | Endpoint | Description |
| :--- | :--- | :--- |
| `POST` | `/api/payments/process` | Mock process a payment |
| `GET` | `/api/payments/{id}` | Get status of a transaction |
| `GET` | `/api/payments/user/{id}` | Get payment audit log for a user |

---
*Developed by Paila Murali Madhav*
