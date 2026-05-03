# How to Add Another Admin User

## Step 1: Generate BCrypt Password Hash

### Option A: Using Online Tool
1. Go to https://bcrypt-generator.com/
2. Enter your desired password
3. Set rounds to 10
4. Copy the generated hash

### Option B: Using Java Code
```java
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordHashGenerator {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String password = "YourPassword123";
        String hash = encoder.encode(password);
        System.out.println(hash);
    }
}
```

## Step 2: Create SQL File

Create a file named `add_new_admin.sql`:

```sql
USE omni_user;

INSERT INTO users (name, email, password, phone_number, role, is_active, created_at, updated_at)
VALUES (
  'Your Admin Name',
  'youradmin@example.com',
  '$2a$10$YourBCryptHashHere',
  '9876543210',
  'ADMIN',
  1,
  NOW(),
  NOW()
);

SELECT id, name, email, role FROM users WHERE email = 'youradmin@example.com';
```

## Step 3: Execute SQL File

### Using PowerShell:
```powershell
Get-Content add_new_admin.sql | mysql -u root -p"Murali#13"
```

### Using MySQL Command Line:
```bash
mysql -u root -p"Murali#13" < add_new_admin.sql
```

## Step 4: Verify Admin Created

```sql
SELECT id, name, email, phone_number, role, is_active 
FROM omni_user.users 
WHERE role = 'ADMIN';
```

---

## Quick Reference: Common BCrypt Hashes (Testing Only)

| Password | BCrypt Hash |
|----------|-------------|
| Admin@123 | `$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy` |
| admin123 | `$2a$10$8cjz47bjbR4Mn8GMg9IZx.vyjhLXR/SKKMSZ9.mP9vpMu0ssKi8GW` |
| password | `$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy` |

⚠️ **Never use these in production!**

---

## Alternative: Promote Existing User to Admin

```sql
USE omni_user;

UPDATE users 
SET role = 'ADMIN', updated_at = NOW()
WHERE email = 'existing.user@example.com';

SELECT id, name, email, role FROM users WHERE email = 'existing.user@example.com';
```

---

## Database Details

- **Database:** `omni_user`
- **Table:** `users`
- **Required Fields:**
  - `name` (VARCHAR)
  - `email` (VARCHAR, UNIQUE)
  - `password` (VARCHAR, BCrypt hash)
  - `phone_number` (VARCHAR)
  - `role` (ENUM: 'USER', 'ADMIN')
  - `is_active` (TINYINT: 0 or 1)
  - `created_at` (DATETIME)
  - `updated_at` (DATETIME)
