# Admin & User Management Changes

## Overview
This document describes the changes made to ensure that:
1. **Admins can only be created via SQL** (not through the UI)
2. **Admins can block/unblock users** instead of deleting them

---

## Backend Changes

### 1. RegisterRequest DTO
**File:** `user-service/src/main/java/com/example/userservice/dto/RegisterRequest.java`

**Change:** Removed the `role` field from the registration request.

```java
// REMOVED:
@NotNull(message = "Role is required")
private Role role;
```

**Reason:** Users registering through the UI should always be assigned the `USER` role. Admins must be created directly in the database.

---

### 2. UserService.register()
**File:** `user-service/src/main/java/com/example/userservice/service/UserService.java`

**Change:** Always set role to `USER` during registration, ignoring any role from the request.

```java
// BEFORE:
user.setRole(request.getRole() != null ? request.getRole() : Role.USER);

// AFTER:
user.setRole(Role.USER); // Role is always USER; admins are created directly via SQL
```

---

### 3. UserService - New Methods
**File:** `user-service/src/main/java/com/example/userservice/service/UserService.java`

**Added two new methods:**

#### `blockUser(Long id)`
- Sets `isActive = false` for the specified user
- Prevents the user from logging in
- Throws `InvalidDataException` if user is already blocked
- Returns: `"User blocked successfully"`

#### `unblockUser(Long id)`
- Sets `isActive = true` for the specified user
- Restores the user's ability to log in
- Throws `InvalidDataException` if user is already active
- Returns: `"User unblocked successfully"`

---

### 4. AdminController - Replaced Delete with Block/Unblock
**File:** `user-service/src/main/java/com/example/userservice/controller/AdminController.java`

**Removed endpoints:**
- `DELETE /api/admin/users/{id}` (soft delete)
- `DELETE /api/admin/users/{id}/permanent` (hard delete)

**Added endpoints:**
- `PATCH /api/admin/users/{id}/block` - Blocks a user
- `PATCH /api/admin/users/{id}/unblock` - Unblocks a user

Both endpoints require `ADMIN` role via `@PreAuthorize("hasRole('ADMIN')")`.

---

## Frontend Changes

### 1. RegisterRequest Interface
**File:** `frontend/src/app/models/auth.model.ts`

**Change:** Removed `role` field from the interface.

```typescript
// BEFORE:
export interface RegisterRequest {
  name: string;
  email: string;
  password: string;
  role: 'USER' | 'ADMIN';
  phoneNumber: string;
}

// AFTER:
export interface RegisterRequest {
  name: string;
  email: string;
  password: string;
  phoneNumber: string;
}
```

---

### 2. Register Component (TypeScript)
**File:** `frontend/src/app/components/register/register.component.ts`

**Change:** Removed `role` from the form group.

```typescript
// BEFORE:
this.form = this.fb.group({
  name: [...],
  email: [...],
  password: [...],
  role: ['USER', Validators.required],  // ❌ REMOVED
  phoneNumber: [...]
});

// AFTER:
this.form = this.fb.group({
  name: [...],
  email: [...],
  password: [...],
  phoneNumber: [...]
});
```

---

### 3. Register Component (HTML)
**File:** `frontend/src/app/components/register/register.component.html`

**Change:** Removed the "Account Type" dropdown from the registration form.

```html
<!-- ❌ REMOVED THIS SECTION: -->
<div class="form-group">
  <label class="form-label">Account Type</label>
  <select class="form-control" formControlName="role">
    <option value="USER">User</option>
    <option value="ADMIN">Admin</option>
  </select>
</div>
```

---

### 4. UserService
**File:** `frontend/src/app/services/user.service.ts`

**Changes:**

**Removed:**
```typescript
deleteUser(id: number): Observable<string> {
  return this.http.delete<string>(`${this.API}/api/admin/users/${id}`);
}
```

**Added:**
```typescript
blockUser(id: number): Observable<string> {
  return this.http.patch<string>(`${this.API}/api/admin/users/${id}/block`, {});
}

unblockUser(id: number): Observable<string> {
  return this.http.patch<string>(`${this.API}/api/admin/users/${id}/unblock`, {});
}
```

---

### 5. ManageUsersComponent (TypeScript)
**File:** `frontend/src/app/components/admin/manage-users/manage-users.component.ts`

**Changes:**
- Replaced `deleteConfirmId` with `actionConfirmId`
- Added `actionType: 'block' | 'unblock' | null`
- Replaced `deleteUser()` method with `executeAction()` that handles both block and unblock
- Added `confirmBlock()` and `confirmUnblock()` methods
- Updates user's `isActive` status locally after successful action

---

### 6. ManageUsersComponent (HTML)
**File:** `frontend/src/app/components/admin/manage-users/manage-users.component.html`

**Changes:**

**Status Badge:**
```html
<!-- BEFORE: -->
{{ user.isActive ? 'Active' : 'Inactive' }}

<!-- AFTER: -->
{{ user.isActive ? 'Active' : 'Blocked' }}
```

**Actions Column:**
- **Active USER:** Shows "Block" button (yellow/warning)
- **Active ADMIN:** Shows "—" (no action allowed)
- **Blocked USER/ADMIN:** Shows "Unblock" button (green/success)
- Confirmation dialog appears when clicking Block/Unblock

---

## How to Create an Admin User

Since admins can no longer be created through the UI, you must insert them directly into the database:

### SQL Command:
```sql
INSERT INTO omni_recharge.users (name, email, password, phone_number, role, is_active, created_at, updated_at)
VALUES (
  'Admin Name',
  'admin@example.com',
  '$2a$10$encodedPasswordHashHere',  -- Use BCrypt to encode the password
  '9876543210',
  'ADMIN',
  true,
  NOW(),
  NOW()
);
```

### To Generate BCrypt Password Hash:
You can use an online BCrypt generator or run this in a Spring Boot application:

```java
String encodedPassword = new BCryptPasswordEncoder().encode("yourPassword");
System.out.println(encodedPassword);
```

---

## Testing Checklist

### Backend:
- [ ] User registration only creates `USER` role accounts
- [ ] `POST /api/auth/register` with `role: 'ADMIN'` is ignored (always creates USER)
- [ ] `PATCH /api/admin/users/{id}/block` blocks a user successfully
- [ ] `PATCH /api/admin/users/{id}/unblock` unblocks a user successfully
- [ ] Blocked users cannot log in
- [ ] Unblocked users can log in again

### Frontend:
- [ ] Registration form does not show "Account Type" dropdown
- [ ] Admin can see "Block" button for active users
- [ ] Admin can see "Unblock" button for blocked users
- [ ] Admin cannot block other admins (no action button shown)
- [ ] Block/Unblock confirmation dialog works correctly
- [ ] User status updates immediately after block/unblock

---

## API Endpoints Summary

### Public Endpoints:
- `POST /api/auth/register` - Register new user (always creates USER role)
- `POST /api/auth/login` - Login

### Admin Endpoints (Require ADMIN role):
- `GET /api/admin/users` - Get all users
- `GET /api/admin/users/{id}` - Get user by ID
- `PATCH /api/admin/users/{id}/block` - Block a user
- `PATCH /api/admin/users/{id}/unblock` - Unblock a user

---

## Security Notes

1. **Role Assignment:** The `USER` role is hardcoded in the backend registration logic. Even if a malicious request includes `role: 'ADMIN'`, it will be ignored.

2. **Admin Protection:** The frontend prevents admins from blocking other admins by hiding the action button. However, the backend should also validate this to prevent API abuse.

3. **Blocked User Login:** The `UserService.login()` method uses `findByEmailAndIsActiveTrue()`, which automatically prevents blocked users from logging in.

4. **JWT Tokens:** Existing JWT tokens for blocked users remain valid until expiration. Consider implementing a token blacklist or reducing token expiration time for better security.

---

## Future Enhancements

1. **Token Invalidation:** Implement a mechanism to invalidate all active sessions when a user is blocked.
2. **Audit Logging:** Log all block/unblock actions with admin ID and timestamp.
3. **Bulk Actions:** Allow admins to block/unblock multiple users at once.
4. **Block Reason:** Add an optional reason field when blocking a user.
5. **Email Notification:** Send an email to users when they are blocked/unblocked.

---

**Last Updated:** May 2, 2026  
**Author:** Paila Murali Madhav
