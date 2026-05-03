# User Service

The **User Service** manages the identity, authentication, and profile information for all users and administrators in the OmniRecharge system.

## ⚙️ Core Configuration

- **Port**: `8081`
- **Database**: `recharge_user_db` (MySQL)
- **Annotations**: `@Service`, `@RestController`, `@Repository`

## 🛠️ Key Features

- **Authentication**: Handles login and registration. Generates secure JWT tokens for the entire system.
- **Profile Management**: Allows users to view and update their personal details, including mobile numbers (validated for 6-9 starting digits).
- **Admin Management**: Provides an API for listing and managing users (Admin-only).
- **Security**: Uses BCrypt for password hashing.

## 📡 API Endpoints

| Method | Endpoint | Description |
| :--- | :--- | :--- |
| `POST` | `/api/users/register` | Create a new account |
| `POST` | `/api/users/login` | Authenticate and get JWT |
| `GET` | `/api/users/profile` | Get current user details |
| `PUT` | `/api/users/profile` | Update user details |
| `GET` | `/api/users/all` | List all users (Admin only) |

---
*Developed by Paila Murali Madhav*
