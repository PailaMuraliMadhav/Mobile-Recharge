import { Component, OnInit } from '@angular/core';
import { UserService } from '../../../services/user.service';
import { RechargeService } from '../../../services/recharge.service';
import { OperatorService } from '../../../services/operator.service';

@Component({
  selector: 'app-admin-dashboard',
  templateUrl: './admin-dashboard.component.html',
  styleUrls: ['./admin-dashboard.component.css']
})
export class AdminDashboardComponent implements OnInit {
  totalUsers = 0;
  totalRecharges = 0;
  totalOperators = 0;
  totalRevenue = 0;
  recentRecharges: any[] = [];
  analytics: any = null;
  loading = true;

  constructor(
    private userService: UserService,
    private rechargeService: RechargeService,
    private operatorService: OperatorService
  ) {}

  ngOnInit(): void {
    this.loadStats();
  }

  loadStats(): void {
    let loaded = 0;
    const done = () => { if (++loaded === 4) this.loading = false; };

    this.userService.getAllUsers().subscribe({
      next: (users) => { this.totalUsers = users.length; done(); },
      error: done
    });

    this.rechargeService.getAnalytics().subscribe({
      next: (data) => {
        this.analytics = data;
        this.totalRecharges = data.totalRecharges;
        this.totalRevenue = data.totalRevenue;
        done();
      },
      error: done
    });

    this.rechargeService.getAllRecharges().subscribe({
      next: (recharges) => {
        this.recentRecharges = recharges.slice(0, 5);
        done();
      },
      error: done
    });

    this.operatorService.getAllOperators().subscribe({
      next: (ops) => { this.totalOperators = ops.length; done(); },
      error: done
    });
  }

  // Helper methods for the template
  getObjectKeys(obj: any): string[] {
    return obj ? Object.keys(obj) : [];
  }

  getObjectValues(obj: any): any[] {
    return obj ? Object.values(obj) : [];
  }

  getTopOperators(): {name: string, count: number}[] {
    if (!this.analytics?.operatorUsage) return [];
    return Object.entries(this.analytics.operatorUsage)
      .map(([name, count]) => ({ name, count: count as number }))
      .sort((a, b) => b.count - a.count)
      .slice(0, 5);
  }

  getTopPlans(): {name: string, count: number}[] {
    if (!this.analytics?.popularPlans) return [];
    return Object.entries(this.analytics.popularPlans)
      .map(([name, count]) => ({ name, count: count as number }))
      .sort((a, b) => b.count - a.count)
      .slice(0, 5);
  }

  getPaymentStats(): {mode: string, count: number}[] {
    if (!this.analytics?.paymentModeDistribution) return [];
    return Object.entries(this.analytics.paymentModeDistribution)
      .map(([mode, count]) => ({ mode, count: count as number }))
      .sort((a, b) => b.count - a.count);
  }

  getRevenueTrends(): {date: string, amount: number}[] {
    if (!this.analytics?.revenueByDay) return [];
    return Object.entries(this.analytics.revenueByDay)
      .map(([date, amount]) => ({ date, amount: amount as number }))
      .sort((a, b) => a.date.localeCompare(b.date))
      .slice(-7); // Last 7 days
  }

  getStatusClass(status: string): string {
    switch (status) {
      case 'SUCCESS': return 'badge-success';
      case 'FAILED': return 'badge-danger';
      default: return 'badge-warning';
    }
  }
}
