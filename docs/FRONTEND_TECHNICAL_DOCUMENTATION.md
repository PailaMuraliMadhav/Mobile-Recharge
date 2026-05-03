# OmniRecharge — Frontend Technical Documentation

---

## 1. Technology Stack

| Technology | Version | Purpose |
|---|---|---|
| Angular | 17 (non-standalone) | Core SPA framework |
| TypeScript | 5.x | Type-safe development |
| RxJS | 7.x | Reactive programming, async data streams |
| Angular Router | 17 | Client-side SPA navigation |
| Angular HttpClient | 17 | REST API communication |
| Angular Reactive Forms | 17 | Form validation and control |
| Pure CSS3 | — | Styling with CSS Custom Properties (no Bootstrap/Tailwind) |

> **Note:** No third-party CSS frameworks are used. All UI components are hand-crafted with CSS Custom Properties for full design control and a smaller bundle size.

---

## 2. Project Architecture

### Folder Structure

```
frontend/
└── src/
    ├── app/
    │   ├── components/          # 15 UI components (pages + shared)
    │   │   ├── landing/         # Public landing page
    │   │   ├── login/           # Authentication page
    │   │   ├── register/        # Registration page
    │   │   ├── dashboard/       # Main user dashboard
    │   │   ├── recharge/        # Operator & plan selection
    │   │   ├── payment-checkout/# Payment form & confirmation
    │   │   ├── payment-status/  # Transaction result page
    │   │   ├── recharge-history/# Filterable transaction table
    │   │   ├── operators/       # Browse all operators & plans
    │   │   ├── profile/         # View & edit user account
    │   │   ├── navbar/          # Top navigation bar
    │   │   ├── sidebar/         # Fixed left sidebar with stats
    │   │   ├── admin-dashboard/ # Admin overview (ADMIN only)
    │   │   ├── admin-operators/ # Operator management (ADMIN only)
    │   │   └── admin-users/     # User management (ADMIN only)
    │   │
    │   ├── services/            # 7 injectable services
    │   │   ├── auth.service.ts      # Login, logout, JWT management
    │   │   ├── user.service.ts      # Profile, history, admin users
    │   │   ├── operator.service.ts  # Operators and plans
    │   │   ├── recharge.service.ts  # Recharge processing
    │   │   ├── payment.service.ts   # Payment status lookup
    │   │   ├── theme.service.ts     # Dark/light mode toggle
    │   │   └── sidebar.service.ts   # Sidebar open/close state
    │   │
    │   ├── models/              # TypeScript interfaces (match backend DTOs)
    │   │   ├── auth.model.ts        # LoginRequest, LoginResponse, RegisterRequest
    │   │   ├── user.model.ts        # UserProfile, UserSummary
    │   │   ├── operator.model.ts    # Operator, Plan
    │   │   ├── recharge.model.ts    # RechargeRequest, RechargeResponse
    │   │   └── payment.model.ts     # PaymentResponse, TransactionDetail
    │   │
    │   ├── guards/              # Route protection
    │   │   └── auth.guard.ts        # AuthGuard — blocks unauthenticated access
    │   │
    │   ├── interceptors/        # HTTP pipeline
    │   │   └── auth.interceptor.ts  # Injects JWT into every outgoing request
    │   │
    │   ├── pipes/               # Custom Angular pipes
    │   │   └── ist-date.pipe.ts     # Converts UTC timestamps to IST display
    │   │
    │   ├── app-routing.module.ts    # All route definitions
    │   └── app.module.ts            # Root NgModule declarations
    │
    ├── environments/
    │   ├── environment.ts           # Development config (localhost:8080)
    │   └── environment.prod.ts      # Production config
    │
    └── styles.css                   # Global CSS with Custom Properties
```

---

## 3. Complete User Flow

### Route Map

| Route | Component | Access |
|---|---|---|
| `/` | LandingComponent | Public |
| `/register` | RegisterComponent | Public |
| `/login` | LoginComponent | Public |
| `/dashboard` | DashboardComponent | Authenticated |
| `/recharge` | RechargeComponent | Authenticated |
| `/checkout` | PaymentCheckoutComponent | Authenticated |
| `/payment-status/:id` | PaymentStatusComponent | Authenticated |
| `/recharge-history` | RechargeHistoryComponent | Authenticated |
| `/operators` | OperatorsComponent | Authenticated |
| `/profile` | ProfileComponent | Authenticated |
| `/admin` | AdminDashboardComponent | ADMIN role only |
| `/admin/operators` | AdminOperatorsComponent | ADMIN role only |
| `/admin/users` | AdminUsersComponent | ADMIN role only |

### Step-by-Step User Journey

**1. Landing Page (`/`)**
Dark-themed marketing page with a hero section, feature highlights, "How It Works" walkthrough, and a footer with contact information. Unauthenticated users land here first.

**2. Register (`/register`)**
Reactive form with client-side validation (required fields, email format, password strength). On submit, calls `POST /api/auth/register`. On success, redirects to `/login`.

**3. Login (`/login`)**
Submits credentials to `POST /api/auth/login`. Backend returns a JWT token. The token is stored in `localStorage` as a JSON object under the key `currentUser`. User is redirected to `/dashboard`.

**4. Dashboard (`/dashboard`)**
Displays a summary of account stats (total recharges, total spent, recent activity), quick-action buttons (New Recharge, View History), and a recent transactions table.

**5. Recharge (`/recharge`)**
Two-step flow: first select an operator from the list, then select a plan. On plan selection, the user is navigated to `/checkout` with plan details passed via Router state.

**6. Checkout (`/checkout`)**
Reads plan/operator/mobile data from `history.state`. User selects a payment method (Card / UPI / Netbanking) and fills in the relevant form fields. Clicking "Pay ₹XXX" triggers a confirmation dialog before the API call is made.

**7. Payment Status (`/payment-status/:id`)**
Fetches transaction details using the `transactionId` from the URL. Displays a success screen (with email notification notice) or a failure screen (with refund notice and timeline).

**8. Recharge History (`/recharge-history`)**
Paginated, filterable table of all past transactions. Filters include date range, status (SUCCESS / FAILED / PENDING), and operator name.

**9. Operators (`/operators`)**
Browse all registered operators and their available plans. Read-only view for regular users.

**10. Profile (`/profile`)**
View and edit account details (name, email, mobile number). Calls `PATCH /api/users/profile` on save.

**11. Admin Panel (`/admin`, `/admin/operators`, `/admin/users`)**
Restricted to users with `role: 'ADMIN'`. Provides platform-wide stats, full CRUD for operators and plans, and user management (view/delete).

---

## 4. JWT Authentication — Complete Implementation

### 4.1 How JWT Works in This App

1. User submits login credentials.
2. Backend validates credentials and returns a signed JWT.
3. Frontend stores the full response object in `localStorage` under the key `currentUser`.
4. The stored object contains: `userId`, `email`, `role`, `token`, `expiresIn`.
5. Every subsequent HTTP request automatically includes the header: `Authorization: Bearer <token>`.
6. On logout (or 401 response), `localStorage` is cleared and the user is redirected to `/login`.

### 4.2 AuthService (`services/auth.service.ts`)

```typescript
// Stores user in BehaviorSubject for reactive updates across all components
private currentUserSubject = new BehaviorSubject<LoginResponse | null>(
  this.getStoredUser()
);

// Exposed as Observable for components to subscribe to
public currentUser$ = this.currentUserSubject.asObservable();

// On login: persists to localStorage and notifies all subscribers
login(request: LoginRequest): Observable<LoginResponse> {
  return this.http.post<LoginResponse>('/api/auth/login', request).pipe(
    tap(response => {
      localStorage.setItem('currentUser', JSON.stringify(response));
      this.currentUserSubject.next(response);
    })
  );
}

// On logout: clears storage, resets state, redirects to login
logout(): void {
  localStorage.removeItem('currentUser');
  this.currentUserSubject.next(null);
  this.router.navigate(['/login']);
}

// Convenience getters used by interceptor and guard
get isLoggedIn(): boolean {
  return !!this.currentUserSubject.value;
}

get token(): string | null {
  return this.currentUserSubject.value?.token ?? null;
}

get currentUser(): LoginResponse | null {
  return this.currentUserSubject.value;
}
```

### 4.3 AuthInterceptor (`interceptors/auth.interceptor.ts`)

```typescript
// Automatically attaches JWT to EVERY outgoing HTTP request
intercept(request: HttpRequest<unknown>, next: HttpHandler): Observable<HttpEvent<unknown>> {
  const token = this.authService.token;

  if (token) {
    request = request.clone({
      setHeaders: {
        Authorization: `Bearer ${token}`
      }
    });
  }

  // Auto-logout on 401 Unauthorized (expired or invalid token)
  return next.handle(request).pipe(
    catchError((error: HttpErrorResponse) => {
      if (error.status === 401) {
        this.authService.logout();
      }
      return throwError(() => error);
    })
  );
}
```

The interceptor is registered in `app.module.ts` as an `HTTP_INTERCEPTORS` provider with `multi: true`, ensuring it runs for every `HttpClient` call in the application.

### 4.4 AuthGuard (`guards/auth.guard.ts`)

```typescript
// Protects routes — redirects to /login if not authenticated
canActivate(route: ActivatedRouteSnapshot): boolean {
  if (!this.authService.isLoggedIn) {
    this.router.navigate(['/login']);
    return false;
  }

  // Role-based access: ADMIN routes check the user's role claim
  const requiredRole = route.data['role'];
  if (requiredRole && this.authService.currentUser?.role !== requiredRole) {
    this.router.navigate(['/dashboard']);
    return false;
  }

  return true;
}
```

### 4.5 Route Protection Configuration

```typescript
// app-routing.module.ts
const routes: Routes = [
  { path: '',        component: LandingComponent },
  { path: 'login',   component: LoginComponent },
  { path: 'register',component: RegisterComponent },

  // Authenticated routes
  { path: 'dashboard',        component: DashboardComponent,       canActivate: [AuthGuard] },
  { path: 'recharge',         component: RechargeComponent,        canActivate: [AuthGuard] },
  { path: 'checkout',         component: PaymentCheckoutComponent, canActivate: [AuthGuard] },
  { path: 'payment-status/:id', component: PaymentStatusComponent, canActivate: [AuthGuard] },
  { path: 'recharge-history', component: RechargeHistoryComponent, canActivate: [AuthGuard] },
  { path: 'operators',        component: OperatorsComponent,       canActivate: [AuthGuard] },
  { path: 'profile',          component: ProfileComponent,         canActivate: [AuthGuard] },

  // Admin-only routes (role check inside AuthGuard)
  { path: 'admin',            component: AdminDashboardComponent,  canActivate: [AuthGuard], data: { role: 'ADMIN' } },
  { path: 'admin/operators',  component: AdminOperatorsComponent,  canActivate: [AuthGuard], data: { role: 'ADMIN' } },
  { path: 'admin/users',      component: AdminUsersComponent,      canActivate: [AuthGuard], data: { role: 'ADMIN' } },
];
```

---

## 5. Backend Connection — API Integration

### 5.1 Environment Configuration

```typescript
// environment.ts (development)
export const environment = {
  production: false,
  apiUrl: 'http://localhost:8080'  // All requests go through the API Gateway
};

// environment.prod.ts (production / Docker)
export const environment = {
  production: true,
  apiUrl: 'http://api-gateway:8080'
};
```

All services inject `environment.apiUrl` as the base URL, so switching between dev and prod requires no code changes — only a build flag (`--configuration=production`).

### 5.2 Service Layer — How Each Service Connects

**AuthService** → User Service (via API Gateway)

| Method | Endpoint | Auth Required |
|---|---|---|
| POST | `/api/auth/register` | No |
| POST | `/api/auth/login` | No |

**UserService** → User Service (via API Gateway)

| Method | Endpoint | Auth Required |
|---|---|---|
| GET | `/api/users/profile` | JWT |
| PATCH | `/api/users/profile` | JWT |
| GET | `/api/users/recharge-history` | JWT |
| GET | `/api/admin/users` | JWT + ADMIN role |

**OperatorService** → Operator Service (via API Gateway)

| Method | Endpoint | Auth Required |
|---|---|---|
| GET | `/api/operators` | JWT |
| GET | `/api/operators/{id}/plans` | JWT |
| POST | `/api/admin/operators` | JWT + ADMIN role |
| POST | `/api/admin/operators/{id}/plans` | JWT + ADMIN role |
| PUT | `/api/admin/operators/{id}` | JWT + ADMIN role |
| DELETE | `/api/admin/operators/{id}` | JWT + ADMIN role |

**RechargeService** → Recharge Service (via API Gateway)

| Method | Endpoint | Auth Required |
|---|---|---|
| POST | `/api/recharges` | JWT |
| GET | `/api/recharges/user/{userId}` | JWT |

**PaymentService** → Payment Service (via API Gateway)

| Method | Endpoint | Auth Required |
|---|---|---|
| GET | `/api/payments/{transactionId}` | JWT |
| GET | `/api/payments/user/{userId}` | JWT |

### 5.3 Request Flow Diagram

```
Angular Component
      │
      ▼
  Service Method
  (e.g., rechargeService.processRecharge(request))
      │
      ▼
  HttpClient.post('http://localhost:8080/api/recharges', body)
      │
      ▼
  AuthInterceptor
  → Clones request
  → Adds header: Authorization: Bearer <JWT>
      │
      ▼
  API Gateway (:8080)
  → Validates JWT signature
  → Extracts role claims
  → Routes to correct microservice
      │
      ▼
  Recharge Service (:8083)
  → Creates recharge record (status: PENDING)
  → Calls Payment Service (:8084) via OpenFeign
      │
      ▼
  Payment Service (:8084)
  → Processes payment
  → Updates recharge status (SUCCESS / FAILED)
  → Publishes event to RabbitMQ
      │
      ▼
  Notification Service (:8085)
  → Consumes RabbitMQ message
  → Sends confirmation email to user
      │
      ▼
  Response propagates back through Gateway → Angular
      │
      ▼
  Component receives transactionId
  → Navigates to /payment-status/:transactionId
```

---

## 6. CORS Configuration

CORS is configured **exclusively in the API Gateway** — not in individual microservices.

```yaml
# API Gateway application.properties
spring.cloud.gateway.globalcors.corsConfigurations.[/**].allowedOrigins=http://localhost:4200,http://frontend:80
spring.cloud.gateway.globalcors.corsConfigurations.[/**].allowedMethods=GET,POST,PUT,PATCH,DELETE,OPTIONS
spring.cloud.gateway.globalcors.corsConfigurations.[/**].allowedHeaders=*
spring.cloud.gateway.globalcors.corsConfigurations.[/**].allowCredentials=true
```

Individual microservices (Operator, Recharge, Payment) have CORS **disabled**:

```java
// In each downstream service's SecurityConfig
http.cors(cors -> cors.disable())
```

**Why this matters:** If CORS were enabled in both the Gateway and downstream services, the browser would receive duplicate `Access-Control-Allow-Origin` headers, causing a CORS error even for valid requests. Centralizing CORS in the Gateway is the correct pattern for a microservices architecture.

---

## 7. Layout Architecture

### 7.1 App Shell

The root `AppComponent` acts as the application shell. It conditionally renders the sidebar and navbar based on authentication state:

```html
<!-- app.component.html -->
<app-sidebar *ngIf="authService.isLoggedIn"></app-sidebar>
<app-navbar  *ngIf="authService.isLoggedIn"></app-navbar>

<main class="main-content" [class.authenticated]="authService.isLoggedIn">
  <router-outlet></router-outlet>
</main>
```

```css
/* Authenticated layout */
.main-content.authenticated {
  margin-left: 240px;   /* Sidebar width */
  padding-top: 64px;    /* Navbar height */
}
```

### 7.2 Public Pages (Landing / Login / Register)

- No sidebar or navbar rendered
- Full-viewport dark-themed layouts
- Each page contains its own inline navigation (logo + CTA buttons)
- Designed to be visually distinct from the authenticated app shell

### 7.3 Authenticated Pages

- Sidebar is always visible on desktop (fixed, 240px wide)
- On mobile, the sidebar slides in via a hamburger toggle in the navbar
- `SidebarService` manages the open/close state using a `BehaviorSubject<boolean>`
- The navbar subscribes to `SidebarService` to toggle the hamburger icon state

```typescript
// sidebar.service.ts
private isOpenSubject = new BehaviorSubject<boolean>(false);
isOpen$ = this.isOpenSubject.asObservable();

toggle(): void {
  this.isOpenSubject.next(!this.isOpenSubject.value);
}
```

---

## 8. Sidebar Analytics

The sidebar loads real-time user statistics on initialization by chaining two service calls:

```typescript
// sidebar.component.ts
totalRecharges = 0;
successCount   = 0;
totalSpent     = 0;

loadStats(): void {
  this.userService.getProfile().subscribe(user => {
    this.rechargeService.getRechargeHistory(user.id).subscribe(recharges => {
      this.totalRecharges = recharges.length;

      const successful = recharges.filter(r => r.status === 'SUCCESS');
      this.successCount = successful.length;
      this.totalSpent   = successful.reduce((sum, r) => sum + Number(r.amount), 0);
    });
  });
}
```

These stats are displayed in the sidebar footer area and update automatically after each new recharge (the sidebar re-fetches on navigation events).

---

## 9. Payment Checkout Flow

```
User selects a plan on /recharge
      │
      ▼
Router.navigate(['/checkout'], {
  state: {
    plan,
    operatorId,
    planId,
    mobileNumber,
    userId
  }
})
      │
      ▼
/checkout component initializes
→ Reads data from history.state (window.history.state)
→ If state is missing (direct URL access), redirects to /recharge
      │
      ▼
User selects payment method:
  ● Card      → Card number, expiry, CVV fields
  ● UPI       → UPI ID field
  ● Netbanking → Bank selection dropdown
      │
      ▼
User clicks "Pay ₹XXX"
→ Confirmation dialog appears (modal overlay)
→ Shows: Plan name, Amount, Mobile number, Payment method
      │
      ▼
User clicks "Confirm Payment"
→ rechargeService.processRecharge(request) called
→ Request body: { userId, operatorId, planId, mobileNumber, paymentMode, amount }
      │
      ▼
Backend processes:
  1. Recharge record created (status: PENDING)
  2. Payment Service called via Feign
  3. Payment processed → status updated (SUCCESS / FAILED)
  4. RabbitMQ event published → email sent
      │
      ▼
Response returns transactionId
→ Router.navigate(['/payment-status', transactionId])
      │
      ▼
Payment Status page:
→ Fetches full transaction details via GET /api/payments/{transactionId}
→ SUCCESS: Shows green confirmation + "Check your email" notice
→ FAILED:  Shows red failure screen + "Refund within 3-5 business days" notice
```

---

## 10. IST Timezone Pipe

### Problem

The Spring Boot backend stores timestamps as `LocalDateTime` without timezone information. When serialized to JSON, the value looks like:

```
"2026-05-01T01:41:00.123"
```

JavaScript's `new Date()` interprets a string without a timezone suffix as **local time** (the browser's OS timezone), not UTC. This causes incorrect time display for users in different timezones.

### Solution

```typescript
// pipes/ist-date.pipe.ts
@Pipe({ name: 'istDate' })
export class IstDatePipe implements PipeTransform {

  constructor(private datePipe: DatePipe) {}

  transform(value: string, format: string = 'dd MMM yyyy, hh:mm a'): string {
    if (!value) return '';

    // Append 'Z' to force JavaScript to treat the timestamp as UTC
    const normalized = value.endsWith('Z') ? value : value + 'Z';
    const date = new Date(normalized);

    // Display in IST (+05:30) using Angular's DatePipe
    return this.datePipe.transform(date, format, '+0530') ?? '';
  }
}
```

### Usage in Templates

```html
<!-- Recharge history table -->
<td>{{ r.createdAt | istDate }}</td>

<!-- Custom format -->
<td>{{ r.createdAt | istDate:'dd MMM yyyy, hh:mm a' }}</td>
```

This ensures all timestamps are displayed consistently in Indian Standard Time regardless of the user's browser locale.

---

## 11. Theme System

### CSS Custom Properties (Global)

```css
/* styles.css */
:root {
  /* Brand colors */
  --primary:       #f97316;   /* Orange — matches OmniRecharge logo */
  --primary-dark:  #ea580c;
  --primary-light: #fed7aa;
  --accent:        #0ea5e9;   /* Blue — matches logo circular arrows */

  /* Light mode defaults */
  --bg:            #f8fafc;
  --bg-card:       #ffffff;
  --text-primary:  #1e293b;
  --text-secondary:#64748b;
  --border:        #e2e8f0;
}

/* Dark mode — applied by ThemeService */
body.dark-mode {
  --bg:            #0c0a09;
  --bg-card:       #1e1510;
  --bg-card-hover: #2a1f14;
  --text-primary:  #fef3c7;
  --text-secondary:#d97706;
  --border:        #44403c;
}
```

### ThemeService

```typescript
// services/theme.service.ts
@Injectable({ providedIn: 'root' })
export class ThemeService {
  private isDark = false;

  constructor() {
    // Restore preference from localStorage on app start
    const saved = localStorage.getItem('theme');
    if (saved === 'dark') {
      this.enableDark();
    }
  }

  toggle(): void {
    this.isDark ? this.enableLight() : this.enableDark();
  }

  private enableDark(): void {
    document.body.classList.add('dark-mode');
    localStorage.setItem('theme', 'dark');
    this.isDark = true;
  }

  private enableLight(): void {
    document.body.classList.remove('dark-mode');
    localStorage.setItem('theme', 'light');
    this.isDark = false;
  }
}
```

> **Design note:** The sidebar is always rendered in dark tones regardless of the selected theme. This provides consistent branding and visual separation between the navigation chrome and the content area.

---

## 12. Confirmation Dialogs

Two confirmation dialogs are implemented using Angular's `*ngIf` pattern with a `showConfirm` boolean flag and a modal overlay — no third-party dialog library required.

### 1. Logout Confirmation (NavbarComponent)

Prevents accidental logout, especially important during active recharge sessions.

```html
<!-- navbar.component.html -->
<div class="modal-overlay" *ngIf="showLogoutConfirm">
  <div class="confirm-dialog">
    <h3>Confirm Logout</h3>
    <p>Are you sure you want to log out?</p>
    <div class="dialog-actions">
      <button class="btn-secondary" (click)="showLogoutConfirm = false">Cancel</button>
      <button class="btn-danger"    (click)="confirmLogout()">Logout</button>
    </div>
  </div>
</div>
```

### 2. Payment Confirmation (PaymentCheckoutComponent)

Displays a summary of the transaction before funds are committed. Shows plan name, amount, mobile number, and selected payment method.

```html
<!-- payment-checkout.component.html -->
<div class="modal-overlay" *ngIf="showPaymentConfirm">
  <div class="confirm-dialog">
    <h3>Confirm Payment</h3>
    <div class="summary">
      <p><strong>Plan:</strong> {{ selectedPlan?.name }}</p>
      <p><strong>Amount:</strong> ₹{{ selectedPlan?.price }}</p>
      <p><strong>Mobile:</strong> {{ mobileNumber }}</p>
      <p><strong>Method:</strong> {{ paymentMethod }}</p>
    </div>
    <div class="dialog-actions">
      <button class="btn-secondary" (click)="showPaymentConfirm = false">Cancel</button>
      <button class="btn-primary"   (click)="confirmPayment()">Confirm Payment</button>
    </div>
  </div>
</div>
```

---

## 13. Admin Panel

Admin routes are protected by both `AuthGuard` (authentication check) and `data: { role: 'ADMIN' }` (role check). A regular user who navigates to `/admin` is silently redirected to `/dashboard`.

### Admin Capabilities

**Admin Dashboard (`/admin`)**
- Platform-wide statistics: total users, total recharges, total revenue
- Recent activity feed

**Operator Management (`/admin/operators`)**
- View all operators with plan counts
- Add new operator (name, logo URL, description)
- Edit existing operator details
- Delete operator (with confirmation dialog)
- Add plans to an operator (name, price, validity, data, description)
- Edit and delete individual plans

**User Management (`/admin/users`)**
- View all registered users (name, email, mobile, join date, recharge count)
- Delete user accounts (with confirmation dialog)

---

## 14. Error Handling

| Scenario | Handling Strategy |
|---|---|
| 401 Unauthorized | AuthInterceptor auto-calls `logout()` → redirects to `/login` |
| 504 Gateway Timeout | User-friendly message: "Service temporarily unavailable. Check your history for status." |
| Form validation errors | Inline error messages below each field (shown on `touched` state) |
| API error responses | Error message extracted from response body and displayed in an alert banner |
| Payment failure | Dedicated failure screen with refund timeline notice |
| Payment cancellation | Toast message: "Payment cancelled. No money was deducted." |
| Missing checkout state | Redirect to `/recharge` if `history.state` is empty (direct URL access) |
| Network offline | Generic error banner with retry suggestion |

---

## 15. Docker Integration

### Multi-Stage Dockerfile

```dockerfile
# Stage 1: Build Angular app
FROM node:20-alpine AS builder
WORKDIR /app
COPY package*.json ./
RUN npm ci --legacy-peer-deps
COPY . .
RUN ./node_modules/.bin/ng build --configuration=production

# Stage 2: Serve with Nginx
FROM nginx:alpine
COPY --from=builder /app/dist/frontend/browser /usr/share/nginx/html
COPY nginx.conf /etc/nginx/conf.d/default.conf
EXPOSE 80
```

### Nginx Configuration

```nginx
server {
    listen 80;
    root /usr/share/nginx/html;
    index index.html;

    # Angular SPA routing — all paths serve index.html
    location / {
        try_files $uri $uri/ /index.html;
    }

    # Cache static assets aggressively
    location ~* \.(js|css|png|jpg|ico|woff2)$ {
        expires 1y;
        add_header Cache-Control "public, immutable";
    }
}
```

The `try_files $uri $uri/ /index.html` directive is critical for Angular SPA routing. Without it, a direct browser navigation to `/dashboard` would return a 404 from Nginx, since there is no physical `dashboard/index.html` file — Angular handles all routing client-side.

### Docker Compose Integration

```yaml
# docker-compose.yml (frontend service)
frontend:
  build:
    context: ./frontend
    dockerfile: Dockerfile
  ports:
    - "80:80"
  depends_on:
    - api-gateway
  networks:
    - app-network
```

---

## 16. Key Design Decisions

| Decision | Reason |
|---|---|
| Pure CSS (no Tailwind/Bootstrap) | Requested by project spec; full design control; no unused CSS in bundle |
| CSS Custom Properties for theming | Single source of truth for all colors; theme switching requires only a class toggle on `body` |
| JWT stored in localStorage | Standard SPA pattern; simple to implement; cleared on logout and 401 |
| BehaviorSubject for auth state | Reactive pattern; all components (navbar, sidebar, guards) auto-update on login/logout without polling |
| API Gateway as single entry point | All frontend requests target `:8080`; CORS configured once; downstream services are not exposed |
| IST timezone pipe | Backend stores `LocalDateTime` (no timezone); pipe appends `Z` to force UTC parsing, then formats in `+0530` |
| Sidebar always dark | Consistent branding regardless of light/dark mode preference; clear visual separation from content |
| Confirmation dialogs for logout and payment | UX best practice for irreversible or financial actions; prevents accidental data loss or unintended charges |
| Non-standalone components (NgModule) | Angular 17 supports both; NgModule chosen for compatibility with the project's existing module structure |
| `--legacy-peer-deps` in Docker build | Resolves peer dependency conflicts in Angular 17 ecosystem without ejecting from the Angular CLI |

---

*Document version: 1.0 — OmniRecharge Frontend, Angular 17*
