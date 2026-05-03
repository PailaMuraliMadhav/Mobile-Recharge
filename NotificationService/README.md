# Notification Service

The **Notification Service** handles all outbound communications for the OmniRecharge platform. It is an event-driven service that reacts to system triggers.

## ⚙️ Core Configuration

- **Port**: `8085`
- **Messaging**: **RabbitMQ** (Queue: `recharge.success.queue`)
- **Email**: **Google SMTP** Integration.

## 🛠️ Key Features

- **Event Consumption**: Listens for `RechargeSuccessEvent` messages on RabbitMQ.
- **Email Automation**: Automatically sends formatted confirmation emails to users once a recharge is successful.
- **Service Decoupling**: Ensures that the core Recharge logic is not slowed down by email sending (which is handled asynchronously).

## 📡 Messaging Logic

- **`@RabbitListener`**: Constantly monitors the RabbitMQ queue for new messages.
- **`JavaMailSender`**: Used to connect to Google's SMTP servers and dispatch emails.

## 📁 Key Components
- `NotificationConsumer`: The listener class that handles RabbitMQ payloads.
- `EmailService`: The utility class that handles SMTP configuration and message formatting.

---
*Developed by Paila Murali Madhav*
