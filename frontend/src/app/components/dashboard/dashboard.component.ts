import { Component, OnInit } from '@angular/core';
import { AuthService } from '../../services/auth.service';
import { RechargeService } from '../../services/recharge.service';
import { UserService } from '../../services/user.service';
import { RechargeResponse } from '../../models/recharge.model';
import { UserResponse } from '../../models/auth.model';

@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.css']
})
export class DashboardComponent implements OnInit {
  user: UserResponse | null = null;
  recentRecharges: RechargeResponse[] = [];
  loading = true;
  totalRecharges = 0;
  totalSpent = 0;
  successCount = 0;

  constructor(
    public authService: AuthService,
    private rechargeService: RechargeService,
    private userService: UserService
  ) {}

  ngOnInit(): void {
    this.loadData();
  }

  loadData(): void {
    this.userService.getProfile().subscribe({
      next: (user) => {
        this.user = user;
        this.loadRecharges(user.id);
      },
      error: () => { this.loading = false; }
    });
  }

  loadRecharges(userId: number): void {
    this.rechargeService.getRechargeHistory(userId).subscribe({
      next: (recharges) => {
        this.recentRecharges = recharges.slice(0, 5);
        this.totalRecharges = recharges.length;
        this.totalSpent = recharges
          .filter(r => r.status === 'SUCCESS')
          .reduce((sum, r) => sum + Number(r.amount), 0);
        this.successCount = recharges.filter(r => r.status === 'SUCCESS').length;
        this.loading = false;
      },
      error: () => { this.loading = false; }
    });
  }

  get firstName(): string {
    if (!this.user?.name) return 'there';
    const parts = this.user.name.split(' ');
    return parts[0] || this.user.name;
  }

  getTimeGreeting(): string {
    const h = new Date().getHours();
    if (h < 12) return 'Good morning';
    if (h < 17) return 'Good afternoon';
    return 'Good evening';
  }

  getStatusClass(status: string): string {
    switch (status) {
      case 'SUCCESS': return 'badge-success';
      case 'FAILED':  return 'badge-danger';
      default:        return 'badge-warning';
    }
  }

  logout(): void {
    this.authService.logout();
  }
}
