import { Component } from '@angular/core';
import { AuthService } from '../../services/auth.service';
import { SidebarService } from '../../services/sidebar.service';
import { ThemeService } from '../../services/theme.service';

@Component({
  selector: 'app-navbar',
  templateUrl: './navbar.component.html',
  styleUrls: ['./navbar.component.css']
})
export class NavbarComponent {
  showLogoutConfirm = false;

  constructor(
    public authService: AuthService,
    private sidebarService: SidebarService,
    private themeService: ThemeService
  ) {}

  get isDark(): boolean { return this.themeService.isDark; }
  toggleTheme(): void   { this.themeService.toggleDarkMode(); }

  get userName(): string {
    return this.authService.currentUser?.email?.split('@')[0] ?? 'User';
  }

  get userRole(): string {
    return this.authService.currentUser?.role ?? '';
  }

  toggleSidebar(): void { this.sidebarService.toggle(); }
  requestLogout(): void { this.showLogoutConfirm = true; }
  confirmLogout(): void { this.showLogoutConfirm = false; this.authService.logout(); }
  cancelLogout(): void  { this.showLogoutConfirm = false; }
}
