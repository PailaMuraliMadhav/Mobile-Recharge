import { Component, OnInit, NgZone } from '@angular/core';
import { Router } from '@angular/router';
import { RechargeService } from '../../services/recharge.service';
import { AuthService } from '../../services/auth.service';
import { UserService } from '../../services/user.service';
import { PlanResponse } from '../../models/operator.model';
import { ToastService } from '../../services/toast.service';
import { PaymentService } from '../../services/payment.service';
import { environment } from '../../../environments/environment';

declare var Razorpay: any;

@Component({
  selector: 'app-payment-checkout',
  templateUrl: './payment-checkout.component.html',
  styleUrls: ['./payment-checkout.component.css']
})
export class PaymentCheckoutComponent implements OnInit {
  mobileNumber = '';
  operatorId   = 0;
  planId       = 0;
  plan: PlanResponse | null = null;
  userId       = 0;

  loading      = false;
  error        = '';

  paymentSuccess = false;
  successDetails: any = null;

  constructor(
    private rechargeService: RechargeService,
    private paymentService: PaymentService,
    private userService: UserService,
    public authService: AuthService,
    private toast: ToastService,
    public router: Router,
    private ngZone: NgZone
  ) {}

  ngOnInit(): void {
    const nav = history.state;
    if (!nav?.plan) { 
      this.router.navigate(['/recharge']); 
      return; 
    }
    this.mobileNumber = nav.mobileNumber;
    this.operatorId   = nav.operatorId;
    this.planId       = nav.planId;
    this.plan         = nav.plan;
    this.userId       = nav.userId;
  }

  onPayClick(): void {
    this.loading     = true;
    this.error       = '';

    const cleanMobile = this.mobileNumber.replace(/\D/g, '').slice(-10);

    const rechargeRequest = {
      userId:       this.userId,
      operatorId:   this.operatorId,
      planId:       this.planId,
      mobileNumber: cleanMobile,
      paymentMode:  'RAZORPAY'
    };

    this.rechargeService.processRecharge(rechargeRequest).subscribe({
      next: (res) => {
        if (res.status === 'PENDING' && res.razorpayOrderId) {
          this.openRazorpay(res);
        } else if (res.status === 'SUCCESS') {
          this.handleSuccess(res);
        } else {
          this.loading = false;
          this.error = 'Payment failed. Please try again.';
          this.toast.error(this.error);
        }
      },
      error: (err) => {
        this.loading = false;
        this.error = err?.error?.message || 'Payment processing failed. Please try again.';
        this.toast.error(this.error);
      }
    });
  }

  private openRazorpay(res: any): void {
    const options = {
      key: environment.razorpayKeyId,
      amount: res.amount * 100,
      currency: 'INR',
      name: 'OmniRecharge',
      description: `Recharge for ${res.mobileNumber}`,
      order_id: res.razorpayOrderId,
      handler: (response: any) => {
        this.ngZone.run(() => {
          this.verifyRazorpay(response);
        });
      },
      prefill: {
        contact: res.mobileNumber,
        email: this.authService.currentUser?.email || ''
      },
      theme: { color: '#6366f1' },
      modal: {
        ondismiss: () => {
          this.loading = false;
          this.toast.info('Payment window closed');
        }
      }
    };
    const rzp = new Razorpay(options);
    this.toast.info('Redirecting to secure gateway...');
    rzp.open();
  }

  private verifyRazorpay(response: any): void {
    this.loading = true;
    this.paymentService.verifyRazorpayPayment(response).subscribe({
      next: (verifyRes) => {
        this.handleSuccess(verifyRes);
      },
      error: (err) => {
        this.loading = false;
        this.error = 'Payment verification failed.';
        this.toast.error(this.error);
      }
    });
  }

  private handleSuccess(res: any): void {
    this.loading = false;
    this.paymentSuccess = true;
    this.successDetails = {
      message: res.message || 'Recharge Successful',
      transactionId: res.transactionId || 'N/A',
      amount: res.amount || this.plan?.price,
      planName: this.plan?.name,
      mobileNumber: this.mobileNumber
    };
    this.toast.success('Recharge successful!');
    this.toast.info('Confirmation email has been sent!');
    console.log('Payment Successful. Receipt:', this.successDetails);
  }


  goToHistory(): void {
    this.router.navigate(['/history']);
  }

  goToRecharge(): void {
    this.router.navigate(['/recharge']);
  }
}
