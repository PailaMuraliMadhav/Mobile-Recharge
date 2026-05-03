# OmniRecharge Frontend - Angular 18

The OmniRecharge frontend is a modern, responsive single-page application (SPA) built with **Angular 18**. It is designed with a "Mobile-First" approach, featuring a premium custom design system and seamless state management.

## 🎨 Design System
- **Pure CSS**: No heavy frameworks like Bootstrap or Tailwind. Every component is styled using vanilla CSS for maximum performance and flexibility.
- **Glassmorphism**: Uses subtle transparency, blurs, and borders to create a modern, layered look.
- **Dark Mode**: Fully integrated dark theme controlled via a dedicated `ThemeService`.
- **Micro-animations**: Smooth transitions, hover effects, and loading skeletons to enhance the user experience.

## 🏗️ Architecture

### **Core Modules**
- `AppModule`: The main module orchestrating all components and services.
- `AppRoutingModule`: Centralized routing with `AuthGuard` for secure pages.

### **Key Components**
- **Landing**: A feature-rich landing page with SEO-friendly sections.
- **Dashboard**: The user's home base, showing transaction analytics and quick access tiles.
- **Recharge**: A multi-step flow for selecting numbers, operators, and plans.
- **Payment Checkout**: A secure interface for processing mock transactions (UPI, Card, Netbanking).
- **Admin Panel**: Restricted area for managing operators, plans, and users.
- **Sidebar & Navbar**: Dynamic navigation components that adapt to user roles and screen sizes.

### **State & Services**
- `AuthService`: Manages JWT storage, user roles, and authentication state.
- `ThemeService`: Persists dark/light mode preference in `localStorage`.
- `ToastService`: Global notification system using an observable-based message queue.
- `RechargeService`: Orchestrates the communication with the backend API Gateway.

## 🛠️ Key Features

### **1. Advanced Form Validation**
Uses Angular **Reactive Forms** with custom Regex patterns (e.g., mobile numbers starting with 6-9) to ensure data integrity before any API calls.

### **2. Role-Based Access Control (RBAC)**
The UI dynamically changes based on the user's role (USER vs ADMIN). Admin-only routes and components are protected both by frontend guards and backend security.

### **3. Responsive Design**
The sidebar collapses into a hamburger menu on mobile, and all grids (stat cards, service tiles) are fluidly responsive.

## 🚀 Development

### **Setup**
1. Install dependencies:
   ```bash
   npm install
   ```
2. Start the development server:
   ```bash
   npm start
   ```

### **Build**
To create a production build:
```bash
npm run build
```

---
*Developed by Paila Murali Madhav*
