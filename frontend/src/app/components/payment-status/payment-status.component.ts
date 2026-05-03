import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { PaymentService } from '../../services/payment.service';
import { UserService } from '../../services/user.service';
import { PaymentResponseDto } from '../../models/payment.model';

@Component({
  selector: 'app-payment-status',
  templateUrl: './payment-status.component.html',
  styleUrls: ['./payment-status.component.css']
})
export class PaymentStatusComponent implements OnInit {
  transactionId = '';
  payment: PaymentResponseDto | null = null;
  loading = true;
  error = '';

  constructor(
    private route: ActivatedRoute,
    public router: Router,
    private paymentService: PaymentService,
    private userService: UserService
  ) {}

  ngOnInit(): void {
    this.transactionId = this.route.snapshot.paramMap.get('transactionId') || '';

    if (!this.transactionId || this.transactionId === 'null' || this.transactionId === 'undefined') {
      this.loading = false;
      this.error = 'Transaction ID is missing. Please check your recharge history.';
      return;
    }

    // Handle recharge-{id} fallback format (when payment service timed out)
    if (this.transactionId.startsWith('recharge-')) {
      this.loading = false;
      this.payment = {
        message: 'Your recharge is being processed. Check history for status.',
        transactionId: this.transactionId,
        status: 'PENDING',
        paymentMode: '—',
        amount: 0
      } as any;
      return;
    }

    // Try payment service first, then user service as fallback
    this.paymentService.getTransactionStatus(this.transactionId).subscribe({
      next: (data: PaymentResponseDto) => { this.payment = data; this.loading = false; },
      error: () => {
        this.userService.getTransactionStatus(this.transactionId).subscribe({
          next: (data: PaymentResponseDto) => { this.payment = data; this.loading = false; },
          error: () => { this.error = 'Could not fetch transaction status. Check your recharge history.'; this.loading = false; }
        });
      }
    });
  }

  getStatusIcon(status: string): string {
    return status === 'SUCCESS' ? '✅' : status === 'FAILED' ? '❌' : '⏳';
  }

  getStatusClass(status: string): string {
    return status === 'SUCCESS' ? 'status-success' : status === 'FAILED' ? 'status-failed' : 'status-pending';
  }
}
