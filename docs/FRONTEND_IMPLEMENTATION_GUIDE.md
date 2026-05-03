# Frontend Implementation Guide - OmniRecharge Platform

## 📋 Table of Contents
1. [Overview](#overview)
2. [What Was Built](#what-was-built)
3. [Technology Stack](#technology-stack)
4. [Project Structure](#project-structure)
5. [Key Features Implemented](#key-features-implemented)
6. [CORS Configuration Fix](#cors-configuration-fix)
7. [Docker Setup](#docker-setup)
8. [API Integration](#api-integration)
9. [How to Run](#how-to-run)
10. [Testing Guide](#testing-guide)
11. [Troubleshooting](#troubleshooting)

---

## 🎯 Overview

This document details the complete Angular 17 frontend implementation for the OmniRecharge mobile recharge platform, including all components, services, routing, authentication, dark mode, and Docker containerization.

**Project Goal:** Build a production-ready Angular frontend that connects to the existing Spring Boot microservices backend with proper CORS handling, JWT authentication, and a green-themed UI with dark mode support.

---

## 🏗️ What Was Built

### Frontend Application (`/frontend`)
- **Framework:** Angular 17 (non-standalone components)
- **Styling:** External CSS with CSS variables for theming
- **State Management:** RxJS BehaviorSubjects for auth and theme
- **HTTP Client:** Angular HttpClient with interceptors
- **Routing:** Angular Router with guards
- **Build Tool:** Angular CLI with production optimizations

### Complete Feature Set
✅ Landing page with hero section, features, stats, and CTA  
✅ User authentication (login/register) with JWT  
✅ Dashboard with stats and quick actions  
✅ Mobile recharge flow (operator → plan → payment)  
✅ Recharge history with filtering  
✅ Browse all operators and plans  
✅ User profile management  
✅ Admin panel (operators, plans, users management)  
✅ Dark mode toggle with localStorage persistence  
✅ Responsive design (mobile, tablet, desktop)  
✅ CORS-compliant API integration  
✅ Docker containerization with Nginx  

---

## 🛠️ Technology Stack

| Layer | Technology | Version | Purpose |
|-------|-----------|---------|---------|
| **Framework** | Angular | 17.3.0 | Frontend framework |
| **Language** | TypeScript | 5.4.2 | Type-safe JavaScript |
| **Styling** | CSS3 | - | Custom green theme with dark mode |
| **HTTP** | HttpClient | Built-in | API communication |
| **Routing** | Angular Router | Built-in | SPA navigation |
| **Forms** | Reactive Forms | Built-in | Form validation |
| **Build** | Angular CLI | 17.3.17 | Build & dev server |
| **Container** | Docker + Nginx | Alpine | Production deployment |

---

## 📁 Project Structure

```
frontend/
├── src/
│   ├── app/
│   │   ├── components/           # All UI components
│   │   │   ├── landing/          # Landing page
│   │   │   ├── login/            # Login form
│   │   │   ├── register/         # Registration form
│   │   │   ├── navbar/           # Navigation bar with dark mode toggle
│   │   │   ├── dashboard/        # User dashboard
│   │   │   ├── recharge/         # Recharge flow
│   │   │   ├── recharge-history/ # Transaction history
│   │   │   ├── operators/        # Browse plans
│   │   │   ├── profile/          # User profile
│   │   │   ├── payment-status/   # Transaction result
│   │   │   └── admin/            # Admin components
│   │   │       ├── admin-dashboard/
│   │   │       ├── manage-operators/
│   │   │       └── manage-users/
│   │   ├── services/             # Business logic
│   │   │   ├── auth.service.ts
│   │   │   ├── user.service.ts
│   │   │   ├── operator.service.ts
│   │   │   ├── recharge.service.ts
│   │   │   ├── payment.service.ts
│   │   │   └── theme.service.ts
│   │   ├── models/               # TypeScript interfaces
│   │   │   ├── auth.model.ts
│   │   │   ├── operator.model.ts
│   │   │   ├── recharge.model.ts
│   │   │   └── payment.model.ts
│   │   ├── guards/               # Route protection
│   │   │   └── auth.guard.ts
│   │   ├── interceptors/         # HTTP interceptors
│   │   │   └── auth.interceptor.ts
│   │   ├── app-routing.module.ts # Route definitions
│   │   ├── app.module.ts         # Root module
│   │   └── app.component.ts      # Root component
│   ├── environments/             # Environment configs
│   │   ├── environment.ts        # Development
│   │   └── environment.prod.ts   # Production
│   ├── styles.css                # Global styles with green theme
│   └── index.html                # HTML entry point
├── Dockerfile                    # Multi-stage Docker build
├── nginx.conf                    # Nginx configuration
├── .dockerignore                 # Docker ignore rules
├── angular.json                  # Angular CLI config
├── package.json                  # Dependencies
└── tsconfig.json                 # TypeScript config
```

---

## ✨ Key Features Implemented

### 1. **Authentication System**
- **JWT-based authentication** with token storage in localStorage
- **Login & Register** forms with validation
- **Auth Guard** protecting routes based on login status and role
- **Auth Interceptor** auto-attaching Bearer token to all requests
- **Auto-logout** on 401 responses

**Files:**
- `services/auth.service.ts` - Authentication logic
- `guards/auth.guard.ts` - Route protection
- `interceptors/auth.interceptor.ts` - Token injection
- `components/login/` - Login UI
- `components/register/` - Registration UI

### 2. **Dark Mode Toggle**
- **Theme Service** managing dark/light mode state
- **CSS Variables** for dynamic color switching
- **localStorage persistence** - remembers user preference
- **Toggle button** in navbar
- **Green theme** in both light and dark modes

**Implementation:**
```typescript
// theme.service.ts
toggleDarkMode() {
  const next = !this.darkMode.value;
  this.darkMode.next(next);
  localStorage.setItem('darkMode', String(next));
  this.applyTheme(next);
}
```

**CSS Variables:**
```css
:root {
  --primary: #16a34a;        /* Green */
  --bg: #ffffff;
  --text-primary: #0f172a;
}

body.dark-mode {
  --bg: #0a0f0a;
  --text-primary: #f0fdf4;
}
```

### 3. **Recharge Flow**
Complete 3-step recharge process:
1. **Select Operator** - Dropdown of all active operators
2. **Choose Plan** - Grid of plans with price, data, validity
3. **Payment** - Select payment mode and confirm

**Features:**
- Real-time plan loading based on operator selection
- Visual plan selection with hover effects
- Order summary sidebar
- Payment mode selection (UPI, Cards, Wallet)
- Transaction status page

**Files:**
- `components/recharge/` - Main recharge flow
- `components/payment-status/` - Result page
- `services/recharge.service.ts` - API calls

### 4. **Admin Panel**
Full CRUD operations for platform management:

**Manage Operators:**
- Add/Edit/Delete operators
- Add/Edit/Delete plans for each operator
- Modal forms for data entry
- Real-time updates

**Manage Users:**
- View all registered users
- Delete users (soft delete)
- Search and filter functionality

**Files:**
- `components/admin/admin-dashboard/`
- `components/admin/manage-operators/`
- `components/admin/manage-users/`

### 5. **Responsive Design**
- **Mobile-first** approach
- **Breakpoints:** 640px, 768px, 1024px
- **Hamburger menu** on mobile
- **Grid layouts** that collapse on small screens
- **Touch-friendly** buttons and interactions

### 6. **User Experience**
- **Loading spinners** for async operations
- **Error messages** with clear feedback
- **Success notifications** for actions
- **Empty states** with helpful CTAs
- **Form validation** with inline errors
- **Smooth transitions** and animations

---

## 🔧 CORS Configuration Fix

### ❌ The Problem
**Error:** `Access-Control-Allow-Origin header contains multiple values 'http://localhost:4200, http://localhost:4200'`

**Root Cause:** CORS was configured in **multiple places**:
1. API Gateway (correct)
2. User Service (duplicate)
3. Operator Service (duplicate)
4. Recharge Service (duplicate)
5. Payment Service (duplicate)

When a request went through the gateway to a microservice, **both added the CORS header**, causing a duplicate value that browsers reject.

### ✅ The Solution

**Rule:** CORS should be configured **ONLY in the API Gateway** (the entry point). Microservices behind the gateway should **disable CORS** or use the same explicit origins.

#### Changes Made:

**1. API Gateway** (`api-gateway/src/main/java/com/example/Api_Gateway/Config/SecurityConfig.java`)
```java
@Bean
public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration config = new CorsConfiguration();
    // ✅ Explicit origins (NOT wildcard pattern with credentials)
    config.setAllowedOrigins(List.of(
            "http://localhost:4200",
            "http://localhost:80",
            "http://frontend",
            "http://frontend:80"
    ));
    config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
    config.setAllowedHeaders(List.of("*"));
    config.setExposedHeaders(List.of("Authorization", "Content-Type"));
    config.setAllowCredentials(true);
    config.setMaxAge(3600L);

    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", config);
    return source;
}
```

**2. User Service** (`user-service/src/main/java/com/example/userservice/config/SecurityConfig.java`)
```java
// ✅ Keep CORS but match gateway exactly (same explicit origins)
@Bean
public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration config = new CorsConfiguration();
    config.setAllowedOrigins(List.of(
            "http://localhost:4200",
            "http://localhost:80",
            "http://frontend",
            "http://frontend:80"
    ));
    // ... same config as gateway
}
```

**3. Operator, Recharge, Payment Services**
```java
@Bean
public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http
            .csrf(csrf -> csrf.disable())
            .cors(cors -> cors.disable())   // ✅ CORS disabled - gateway handles it
            .authorizeHttpRequests(auth -> auth
                    .anyRequest().permitAll());
    return http.build();
}
```

### Why This Works
1. **Single source of truth** - Only gateway adds CORS headers
2. **No duplicates** - Microservices don't add their own headers
3. **Explicit origins** - No wildcard pattern issues with credentials
4. **Browser compliance** - Single `Access-Control-Allow-Origin` value

### Testing CORS
```bash
# Preflight request (OPTIONS)
curl -X OPTIONS http://localhost:8080/api/auth/login \
  -H "Origin: http://localhost:4200" \
  -H "Access-Control-Request-Method: POST" \
  -H "Access-Control-Request-Headers: Content-Type" \
  -D -

# Should return:
# Access-Control-Allow-Origin: http://localhost:4200  (single value ✅)
```

---

## 🐳 Docker Setup

### Multi-Stage Dockerfile

**Stage 1: Build** (Node 20 Alpine)
```dockerfile
FROM node:20-alpine AS builder
WORKDIR /app
COPY package*.json ./
RUN npm ci --legacy-peer-deps
COPY . .
RUN ./node_modules/.bin/ng build --configuration=production
```

**Stage 2: Serve** (Nginx Alpine)
```dockerfile
FROM nginx:alpine
COPY --from=builder /app/dist/frontend/browser /usr/share/nginx/html
COPY nginx.conf /etc/nginx/conf.d/default.conf
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
```

### Nginx Configuration (`nginx.conf`)
```nginx
server {
    listen 80;
    server_name localhost;
    root /usr/share/nginx/html;
    index index.html;

    # Gzip compression
    gzip on;
    gzip_types text/plain text/css application/json application/javascript text/xml application/xml application/xml+rss text/javascript;

    # Angular routing - redirect all to index.html
    location / {
        try_files $uri $uri/ /index.html;
    }

    # Cache static assets
    location ~* \.(js|css|png|jpg|jpeg|gif|ico|svg|woff|woff2|ttf|eot)$ {
        expires 1y;
        add_header Cache-Control "public, immutable";
    }

    # Security headers
    add_header X-Frame-Options "SAMEORIGIN";
    add_header X-Content-Type-Options "nosniff";
    add_header X-XSS-Protection "1; mode=block";
}
```

### Docker Compose Integration

Added to `docker-compose.yml`:
```yaml
frontend:
  build: ./frontend
  image: mobile-recharge-frontend:latest
  container_name: frontend
  ports:
    - "4200:80"
  restart: on-failure
  depends_on:
    - api-gateway
  networks:
    - microservice-network
  deploy:
    resources:
      limits:
        memory: 128M
```

---

## 🔌 API Integration

### Environment Configuration

**Development** (`environment.ts`):
```typescript
export const environment = {
  production: false,
  apiUrl: 'http://localhost:8080'
};
```

**Production** (`environment.prod.ts`):
```typescript
export const environment = {
  production: true,
  apiUrl: 'http://localhost:8080'
};
```

### Service Architecture

All services follow the same pattern:

```typescript
@Injectable({ providedIn: 'root' })
export class ExampleService {
  private readonly API = environment.apiUrl;

  constructor(private http: HttpClient) {}

  getData(): Observable<DataType> {
    return this.http.get<DataType>(`${this.API}/api/endpoint`);
  }
}
```

### HTTP Interceptor

Automatically attaches JWT token to all requests:

```typescript
@Injectable()
export class AuthInterceptor implements HttpInterceptor {
  intercept(request: HttpRequest<unknown>, next: HttpHandler): Observable<HttpEvent<unknown>> {
    const token = this.authService.token;

    if (token) {
      request = request.clone({
        setHeaders: { Authorization: `Bearer ${token}` }
      });
    }

    return next.handle(request).pipe(
      catchError((error: HttpErrorResponse) => {
        if (error.status === 401) {
          this.authService.logout();
          this.router.navigate(['/login']);
        }
        return throwError(() => error);
      })
    );
  }
}
```

### API Endpoints Used

| Service | Endpoint | Method | Purpose |
|---------|----------|--------|---------|
| **Auth** | `/api/auth/register` | POST | User registration |
| **Auth** | `/api/auth/login` | POST | User login (returns JWT) |
| **User** | `/api/users/profile` | GET | Get current user profile |
| **User** | `/api/users/profile` | PATCH | Update profile |
| **User** | `/api/users/recharge-history` | GET | Get user's recharges |
| **User** | `/api/admin/users` | GET | Get all users (admin) |
| **User** | `/api/admin/users/{id}` | DELETE | Delete user (admin) |
| **Operator** | `/api/operators` | GET | Get all operators |
| **Operator** | `/api/operators/{id}/plans` | GET | Get plans for operator |
| **Operator** | `/api/admin/operators` | POST | Create operator (admin) |
| **Operator** | `/api/admin/operators/{id}` | PUT | Update operator (admin) |
| **Operator** | `/api/admin/operators/{id}/plans` | POST | Add plan (admin) |
| **Recharge** | `/api/recharges` | POST | Process recharge |
| **Recharge** | `/api/recharges/user/{userId}` | GET | Get recharge history |
| **Payment** | `/api/payments/{transactionId}` | GET | Get transaction status |

---

## 🚀 How to Run

### Prerequisites
- Node.js 20+
- npm 10+
- Angular CLI 17
- Docker & Docker Compose (for containerized deployment)

### Local Development

```bash
# Navigate to frontend directory
cd frontend

# Install dependencies
npm install

# Start development server
npm start

# Open browser
# http://localhost:4200
```

### Production Build

```bash
# Build for production
npm run build

# Output will be in dist/frontend/browser/
```

### Docker Build

```bash
# Build Docker image
docker build -t mobile-recharge-frontend:latest ./frontend

# Run container
docker run -p 4200:80 mobile-recharge-frontend:latest
```

### Full Stack with Docker Compose

```bash
# Build and start all services
docker-compose up --build -d

# Check status
docker-compose ps

# View logs
docker-compose logs -f frontend

# Stop all services
docker-compose down
```

### Service URLs

| Service | URL | Description |
|---------|-----|-------------|
| **Frontend** | http://localhost:4200 | Angular UI |
| **API Gateway** | http://localhost:8080 | Backend entry point |
| **Eureka Dashboard** | http://localhost:8761 | Service registry |
| **RabbitMQ Management** | http://localhost:15672 | Message broker UI |
| **Zipkin** | http://localhost:9411 | Distributed tracing |

---

## 🧪 Testing Guide

### Manual Testing Checklist

#### Authentication Flow
- [ ] Register new user with valid data
- [ ] Register with invalid email (should show error)
- [ ] Register with short password (should show error)
- [ ] Register with invalid phone number (should show error)
- [ ] Login with correct credentials
- [ ] Login with wrong password (should show error)
- [ ] Logout and verify redirect to login page

#### User Dashboard
- [ ] View dashboard stats (total recharges, successful, total spent)
- [ ] Click quick action buttons
- [ ] View recent recharges table
- [ ] Empty state when no recharges

#### Recharge Flow
- [ ] Select operator from dropdown
- [ ] Plans load for selected operator
- [ ] Select a plan (visual feedback)
- [ ] Order summary updates
- [ ] Select payment mode
- [ ] Submit recharge
- [ ] View transaction status page

#### Recharge History
- [ ] View all past recharges
- [ ] Search by mobile number
- [ ] Search by transaction ID
- [ ] Filter by status (Success/Pending/Failed)
- [ ] Empty state when no results

#### Browse Operators
- [ ] View all operators in tabs
- [ ] Switch between operators
- [ ] View plans for each operator
- [ ] Click "Recharge" button navigates to recharge page

#### Profile Management
- [ ] View profile information
- [ ] Edit name and phone number
- [ ] Save changes
- [ ] Cancel edit mode

#### Admin Panel (Admin Role Only)
- [ ] View admin dashboard stats
- [ ] Navigate to Manage Operators
- [ ] Add new operator
- [ ] Edit operator
- [ ] Delete operator
- [ ] Add plan to operator
- [ ] Edit plan
- [ ] Delete plan
- [ ] Navigate to Manage Users
- [ ] View all users
- [ ] Search users
- [ ] Delete user

#### Dark Mode
- [ ] Toggle dark mode from navbar
- [ ] Verify all pages render correctly in dark mode
- [ ] Refresh page - dark mode persists
- [ ] Toggle back to light mode

#### Responsive Design
- [ ] Test on mobile (< 640px)
- [ ] Test on tablet (640px - 1024px)
- [ ] Test on desktop (> 1024px)
- [ ] Hamburger menu works on mobile
- [ ] All forms are usable on mobile

### Browser Testing
- [ ] Chrome/Edge (Chromium)
- [ ] Firefox
- [ ] Safari (if available)

### CORS Testing

```bash
# Test preflight
curl -X OPTIONS http://localhost:8080/api/auth/login \
  -H "Origin: http://localhost:4200" \
  -H "Access-Control-Request-Method: POST" \
  -H "Access-Control-Request-Headers: Content-Type" \
  -D -

# Should see single Access-Control-Allow-Origin header
```

---

## 🐛 Troubleshooting

### Issue: CORS Error in Browser Console

**Symptom:**
```
Access to XMLHttpRequest at 'http://localhost:8080/api/auth/login' 
from origin 'http://localhost:4200' has been blocked by CORS policy
```

**Solution:**
1. Verify API Gateway is running: `docker-compose ps`
2. Check CORS config in `api-gateway/SecurityConfig.java`
3. Ensure microservices have CORS disabled (except user-service)
4. Restart API Gateway: `docker restart api-gateway`

### Issue: 404 Not Found on API Calls

**Symptom:** All API calls return 404

**Solution:**
1. Check if API Gateway loaded routes from config-server
2. View gateway logs: `docker logs api-gateway | grep -i route`
3. Verify config-server is healthy: `docker-compose ps`
4. Restart gateway after config-server is healthy

### Issue: JWT Token Not Attached

**Symptom:** 401 Unauthorized on protected routes

**Solution:**
1. Check if token exists in localStorage (DevTools → Application → Local Storage)
2. Verify `AuthInterceptor` is registered in `app.module.ts`
3. Check token format: should be `Bearer <token>`
4. Verify JWT secret matches between services

### Issue: Dark Mode Not Persisting

**Symptom:** Dark mode resets on page refresh

**Solution:**
1. Check localStorage: `localStorage.getItem('darkMode')`
2. Verify `ThemeService` reads from localStorage on init
3. Check browser privacy settings (localStorage enabled)

### Issue: Docker Build Fails

**Symptom:** `ng: command not found` during Docker build

**Solution:**
1. Clear Docker build cache: `docker builder prune -f`
2. Verify Dockerfile uses `./node_modules/.bin/ng` (not just `ng`)
3. Ensure `npm ci` completes successfully
4. Check `package.json` has `@angular/cli` in devDependencies

### Issue: Nginx 404 on Refresh

**Symptom:** Refreshing any route (except `/`) returns 404

**Solution:**
1. Verify `nginx.conf` has `try_files $uri $uri/ /index.html;`
2. Ensure nginx.conf is copied to container
3. Check nginx logs: `docker logs frontend`

### Issue: Services Not Starting

**Symptom:** Docker containers exit immediately

**Solution:**
1. Check logs: `docker-compose logs <service-name>`
2. Verify `.env` file exists with `DB_PASSWORD` and `JWT_SECRET`
3. Ensure MySQL is running on host (for services using `host.docker.internal`)
4. Check port conflicts: `netstat -ano | findstr :<port>`

---

## 📝 Summary of Changes

### Files Created (Frontend)
- **13 Components** with HTML, CSS, TS files
- **6 Services** for business logic
- **4 Model files** for TypeScript interfaces
- **1 Guard** for route protection
- **1 Interceptor** for HTTP requests
- **1 Dockerfile** for containerization
- **1 nginx.conf** for production serving
- **Global styles** with green theme and dark mode

### Files Modified (Backend)
- `api-gateway/SecurityConfig.java` - Fixed CORS with explicit origins
- `user-service/SecurityConfig.java` - Matched gateway CORS config
- `OperatorService/SecurityConfig.java` - Disabled CORS
- `RechargeService/SecurityConfig.java` - Disabled CORS
- `PaymentService/SecurityConfig.java` - Disabled CORS
- `docker-compose.yml` - Added frontend service

### Key Decisions Made

1. **Angular 17 Non-Standalone** - For better module organization
2. **External CSS** - Easier theming and maintenance
3. **Green Theme** - As requested, with dark mode support
4. **JWT in localStorage** - Simple and effective for SPA
5. **Explicit CORS Origins** - Avoid wildcard issues with credentials
6. **Multi-stage Docker** - Smaller production image (Nginx Alpine)
7. **Reactive Forms** - Better validation and type safety
8. **BehaviorSubject** - For auth and theme state management

---

## 🎓 Learning Resources

- [Angular Documentation](https://angular.io/docs)
- [RxJS Guide](https://rxjs.dev/guide/overview)
- [CORS Explained](https://developer.mozilla.org/en-US/docs/Web/HTTP/CORS)
- [JWT Best Practices](https://tools.ietf.org/html/rfc8725)
- [Docker Multi-Stage Builds](https://docs.docker.com/build/building/multi-stage/)
- [Nginx Configuration](https://nginx.org/en/docs/)

---

## 📞 Support

For issues or questions:
1. Check the [Troubleshooting](#troubleshooting) section
2. Review Docker logs: `docker-compose logs -f`
3. Check browser console for errors (F12)
4. Verify all services are running: `docker-compose ps`

---

**Document Version:** 1.0  
**Last Updated:** April 30, 2026  
**Author:** Kiro AI Assistant  
**Project:** OmniRecharge Mobile Recharge Platform
