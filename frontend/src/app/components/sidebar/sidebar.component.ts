import { Component, OnInit, OnDestroy } from '@angular/core';
import { Router, NavigationEnd } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { ThemeService } from '../../services/theme.service';
import { SidebarService } from '../../services/sidebar.service';
import { RechargeService } from '../../services/recharge.service';
import { UserService } from '../../services/user.service';
import { Subscription } from 'rxjs';
import { filter } from 'rxjs/operators';

@Component({
  selector: 'app-sidebar',
  templateUrl: './sidebar.component.html',
  styleUrls: ['./sidebar.component.css']
})
export class SidebarComponent implements OnInit, OnDestroy {
  mobileOpen = false;
  showLogoutConfirm = false;
  private subs = new Subscription();

  // Analytics
  totalRecharges = 0;
  totalSpent = 0;
  successCount = 0;

  navItems = [
    { path: '/dashboard',        icon: '🏠', label: 'Home' },
    { path: '/recharge',         icon: '⚡', label: 'Recharge' },
    { path: '/operators',        icon: '📋', label: 'Plans' },
    { path: '/recharge-history', icon: '🕐', label: 'History' },
    { path: '/profile',          icon: '👤', label: 'Profile' },
  ];

  adminItems = [
    { path: '/admin',           icon: '📈', label: 'Overview' },
    { path: '/admin/operators', icon: '🌐', label: 'Operators' },
    { path: '/admin/users',     icon: '👥', label: 'Users' },
  ];

  constructor(
    public authService: AuthService,
    public themeService: ThemeService,
    private sidebarService: SidebarService,
    private rechargeService: RechargeService,
    private userService: UserService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.subs.add(
      this.sidebarService.open$.subscribe(v => this.mobileOpen = v)
    );
    this.subs.add(
      this.router.events.pipe(filter(e => e instanceof NavigationEnd))
        .subscribe(() => this.sidebarService.close())
    );
    this.loadStats();
  }

  ngOnDestroy(): void { this.subs.unsubscribe(); }

  loadStats(): void {
    if (!this.authService.isLoggedIn) return;
    this.userService.getProfile().subscribe({
      next: (user) => {
        this.rechargeService.getRechargeHistory(user.id).subscribe({
          next: (recharges) => {
            this.totalRecharges = recharges.length;
            this.successCount = recharges.filter(r => r.status === 'SUCCESS').length;
            this.totalSpent = recharges
              .filter(r => r.status === 'SUCCESS')
              .reduce((s, r) => s + Number(r.amount), 0);
          }
        });
      }
    });
  }

  closeMobile(): void { this.sidebarService.close(); }
  requestLogout(): void { this.showLogoutConfirm = true; }
  confirmLogout(): void { this.showLogoutConfirm = false; this.authService.logout(); }
  cancelLogout(): void  { this.showLogoutConfirm = false; }

  get isDark(): boolean { return this.themeService.isDark; }
  toggleTheme(): void   { this.themeService.toggleDarkMode(); }

  get userInitial(): string {
    return this.authService.currentUser?.email?.charAt(0).toUpperCase() ?? '?';
  }
  get userName(): string {
    return this.authService.currentUser?.email?.split('@')[0] ?? 'User';
  }
  get successRate(): number {
    return this.totalRecharges > 0
      ? Math.round((this.successCount / this.totalRecharges) * 100)
      : 0;
  }
}
