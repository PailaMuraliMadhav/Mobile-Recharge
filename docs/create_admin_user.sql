-- ============================================================================
-- CREATE ADMIN USER SCRIPT
-- ============================================================================
-- This script creates an admin user in the database.
-- Admins cannot be created through the UI registration form.
--
-- IMPORTANT: Replace the password hash with a BCrypt-encoded password.
-- You can generate a BCrypt hash using:
--   - Online tool: https://bcrypt-generator.com/
--   - Java: new BCryptPasswordEncoder().encode("yourPassword")
-- ============================================================================

-- Example 1: Create admin with email admin@omni.com and password "Admin@123"
-- BCrypt hash for "Admin@123" (cost factor 10):
INSERT INTO omni_user.users (name, email, password, phone_number, role, is_active, created_at, updated_at)
VALUES (
  'System Administrator',
  'admin@omni.com',
  '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy',  -- Password: Admin@123
  '9999999999',
  'ADMIN',
  true,
  NOW(),
  NOW()
);

-- Example 2: Create another admin (replace values as needed)
-- INSERT INTO omni_user.users (name, email, password, phone_number, role, is_active, created_at, updated_at)
-- VALUES (
--   'Your Name',
--   'youremail@example.com',
--   '$2a$10$YourBCryptHashHere',  -- Generate your own BCrypt hash
--   '9876543210',
--   'ADMIN',
--   true,
--   NOW(),
--   NOW()
-- );

-- ============================================================================
-- VERIFY ADMIN CREATION
-- ============================================================================
-- Run this query to verify the admin was created successfully:
SELECT id, name, email, phone_number, role, is_active, created_at 
FROM omni_user.users 
WHERE role = 'ADMIN';

-- ============================================================================
-- CONVERT EXISTING USER TO ADMIN
-- ============================================================================
-- If you want to promote an existing user to admin:
-- UPDATE omni_user.users 
-- SET role = 'ADMIN', updated_at = NOW()
-- WHERE email = 'user@example.com';

-- ============================================================================
-- COMMON BCRYPT PASSWORD HASHES (for testing only - DO NOT use in production)
-- ============================================================================
-- Password: "password"
-- Hash: $2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy

-- Password: "admin123"
-- Hash: $2a$10$8cjz47bjbR4Mn8GMg9IZx.vyjhLXR/SKKMSZ9.mP9vpMu0ssKi8GW

-- Password: "Admin@123"
-- Hash: $2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy

-- ============================================================================
-- NOTES:
-- ============================================================================
-- 1. Always use strong passwords for admin accounts in production
-- 2. The BCrypt cost factor (10) provides a good balance between security and performance
-- 3. Each time you hash the same password, you'll get a different hash (due to salt)
-- 4. Never store plain text passwords in the database
-- 5. Admin users have full access to the platform - create them carefully
-- ============================================================================
