import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { HttpClientModule, HTTP_INTERCEPTORS } from '@angular/common/http';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { LandingComponent } from './components/landing/landing.component';
import { LoginComponent } from './components/login/login.component';
import { RegisterComponent } from './components/register/register.component';
import { NavbarComponent } from './components/navbar/navbar.component';
import { DashboardComponent } from './components/dashboard/dashboard.component';
import { RechargeComponent } from './components/recharge/recharge.component';
import { RechargeHistoryComponent } from './components/recharge-history/recharge-history.component';
import { OperatorsComponent } from './components/operators/operators.component';
import { ProfileComponent } from './components/profile/profile.component';
import { AdminDashboardComponent } from './components/admin/admin-dashboard/admin-dashboard.component';
import { ManageOperatorsComponent } from './components/admin/manage-operators/manage-operators.component';
import { ManageUsersComponent } from './components/admin/manage-users/manage-users.component';
import { PaymentStatusComponent } from './components/payment-status/payment-status.component';
import { AuthInterceptor } from './interceptors/auth.interceptor';
import { PaymentCheckoutComponent } from './components/payment-checkout/payment-checkout.component';
import { IstDatePipe } from './pipes/ist-date.pipe';
import { SidebarComponent } from './components/sidebar/sidebar.component';
import { ToastComponent } from './components/toast/toast.component';

@NgModule({
  declarations: [
    AppComponent,
    LandingComponent,
    LoginComponent,
    RegisterComponent,
    NavbarComponent,
    DashboardComponent,
    RechargeComponent,
    RechargeHistoryComponent,
    OperatorsComponent,
    ProfileComponent,
    AdminDashboardComponent,
    ManageOperatorsComponent,
    ManageUsersComponent,
    PaymentStatusComponent,
    PaymentCheckoutComponent,
    IstDatePipe,
    SidebarComponent,
    ToastComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    FormsModule,
    ReactiveFormsModule,
    HttpClientModule
  ],
  providers: [
    { provide: HTTP_INTERCEPTORS, useClass: AuthInterceptor, multi: true }
  ],
  bootstrap: [AppComponent]
})
export class AppModule {}
