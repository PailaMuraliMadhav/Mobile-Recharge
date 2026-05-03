import { Component, OnInit } from '@angular/core';
import { RechargeService } from '../../services/recharge.service';
import { AuthService } from '../../services/auth.service';
import { UserService } from '../../services/user.service';
import { RechargeResponse } from '../../models/recharge.model';

@Component({
  selector: 'app-recharge-history',
  templateUrl: './recharge-history.component.html',
  styleUrls: ['./recharge-history.component.css']
})
export class RechargeHistoryComponent implements OnInit {
  recharges: RechargeResponse[] = [];
  filteredRecharges: RechargeResponse[] = [];
  loading = true;
  searchTerm = '';
  filterStatus = '';

  constructor(
    private rechargeService: RechargeService,
    private authService: AuthService,
    private userService: UserService
  ) {}

  ngOnInit(): void {
    if (this.authService.isAdmin) {
      this.rechargeService.getAllRecharges().subscribe({
        next: (data) => {
          this.recharges = data;
          this.filteredRecharges = data;
          this.loading = false;
        },
        error: () => { this.loading = false; }
      });
    } else {
      this.userService.getProfile().subscribe({
        next: (user) => {
          this.rechargeService.getRechargeHistory(user.id).subscribe({
            next: (data) => {
              this.recharges = data;
              this.filteredRecharges = data;
              this.loading = false;
            },
            error: () => { this.loading = false; }
          });
        },
        error: () => { this.loading = false; }
      });
    }
  }

  applyFilter(): void {
    this.filteredRecharges = this.recharges.filter(r => {
      const matchSearch = !this.searchTerm ||
        r.mobileNumber.includes(this.searchTerm) ||
        (r.transactionId && r.transactionId.toLowerCase().includes(this.searchTerm.toLowerCase()));
      const matchStatus = !this.filterStatus || r.status === this.filterStatus;
      return matchSearch && matchStatus;
    });
  }

  getStatusClass(status: string): string {
    switch (status) {
      case 'SUCCESS': return 'badge-success';
      case 'FAILED': return 'badge-danger';
      default: return 'badge-warning';
    }
  }
}
