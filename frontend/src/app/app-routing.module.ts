import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { LandingComponent } from './components/landing/landing.component';
import { LoginComponent } from './components/login/login.component';
import { RegisterComponent } from './components/register/register.component';
import { DashboardComponent } from './components/dashboard/dashboard.component';
import { RechargeComponent } from './components/recharge/recharge.component';
import { RechargeHistoryComponent } from './components/recharge-history/recharge-history.component';
import { OperatorsComponent } from './components/operators/operators.component';
import { ProfileComponent } from './components/profile/profile.component';
import { PaymentStatusComponent } from './components/payment-status/payment-status.component';
import { PaymentCheckoutComponent } from './components/payment-checkout/payment-checkout.component';
import { AdminDashboardComponent } from './components/admin/admin-dashboard/admin-dashboard.component';
import { ManageOperatorsComponent } from './components/admin/manage-operators/manage-operators.component';
import { ManageUsersComponent } from './components/admin/manage-users/manage-users.component';
import { AuthGuard } from './guards/auth.guard';

const routes: Routes = [
  { path: '', component: LandingComponent },
  { path: 'login', component: LoginComponent },
  { path: 'register', component: RegisterComponent },
  { path: 'dashboard',        component: DashboardComponent,       canActivate: [AuthGuard] },
  { path: 'recharge',         component: RechargeComponent,        canActivate: [AuthGuard] },
  { path: 'checkout',         component: PaymentCheckoutComponent, canActivate: [AuthGuard] },
  { path: 'recharge-history', component: RechargeHistoryComponent, canActivate: [AuthGuard] },
  { path: 'operators',        component: OperatorsComponent,       canActivate: [AuthGuard] },
  { path: 'profile',          component: ProfileComponent,         canActivate: [AuthGuard] },
  { path: 'payment-status/:transactionId', component: PaymentStatusComponent, canActivate: [AuthGuard] },
  { path: 'admin',            component: AdminDashboardComponent,  canActivate: [AuthGuard], data: { role: 'ADMIN' } },
  { path: 'admin/operators',  component: ManageOperatorsComponent, canActivate: [AuthGuard], data: { role: 'ADMIN' } },
  { path: 'admin/users',      component: ManageUsersComponent,     canActivate: [AuthGuard], data: { role: 'ADMIN' } },
  { path: '**', redirectTo: '' }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule {}
