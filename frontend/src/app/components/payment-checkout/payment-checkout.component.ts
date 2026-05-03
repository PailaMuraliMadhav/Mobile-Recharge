import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { RechargeService } from '../../services/recharge.service';
import { AuthService } from '../../services/auth.service';
import { UserService } from '../../services/user.service';
import { PlanResponse } from '../../models/operator.model';
import { ToastService } from '../../services/toast.service';

type PaymentTab = 'card' | 'upi' | 'netbanking';

const UPI_APPS = [
  { id: 'gpay',    name: 'GPay',    icon: '🟢', upiSuffix: '@okicici' },
  { id: 'phonepe', name: 'PhonePe', icon: '🟣', upiSuffix: '@ybl' },
  { id: 'paytm',   name: 'Paytm',   icon: '🔵', upiSuffix: '@paytm' },
  { id: 'bhim',    name: 'BHIM',    icon: '🟠', upiSuffix: '@upi' },
];

const BANKS = [
  { id: 'SBI',   name: 'State Bank of India', icon: '🏦' },
  { id: 'HDFC',  name: 'HDFC Bank',           icon: '🏦' },
  { id: 'ICICI', name: 'ICICI Bank',           icon: '🏦' },
  { id: 'AXIS',  name: 'Axis Bank',            icon: '🏦' },
  { id: 'KOTAK', name: 'Kotak Mahindra',       icon: '🏦' },
  { id: 'BOB',   name: 'Bank of Baroda',       icon: '🏦' },
];

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

  activeTab: PaymentTab = 'upi';
  loading      = false;
  error        = '';
  banks        = BANKS;
  upiApps      = UPI_APPS;
  selectedBank = '';

  selectedUpiApp = '';
  upiInputMode   = false;
  showConfirm = false;

  cardForm: FormGroup;
  upiForm:  FormGroup;

  constructor(
    private fb: FormBuilder,
    private rechargeService: RechargeService,
    private userService: UserService,
    public authService: AuthService,
    private toast: ToastService,
    public router: Router
  ) {
    this.cardForm = this.fb.group({
      cardNumber: ['', [Validators.required, Validators.pattern(/^\d{16}$/)]],
      cardHolder: ['', Validators.required],
      expiry:     ['', [Validators.required, Validators.pattern(/^(0[1-9]|1[0-2])\/\d{2}$/)]],
      cvv:        ['', [Validators.required, Validators.pattern(/^\d{3,4}$/)]]
    });

    this.upiForm = this.fb.group({
      upiId: ['', [Validators.required, Validators.pattern(/^[\w.\-]+@[\w]+$/)]]
    });
  }

  ngOnInit(): void {
    const nav = history.state;
    if (!nav?.plan) { this.router.navigate(['/recharge']); return; }
    this.mobileNumber = nav.mobileNumber;
    this.operatorId   = nav.operatorId;
    this.planId       = nav.planId;
    this.plan         = nav.plan;
    this.userId       = nav.userId;
  }

  setTab(tab: PaymentTab): void {
    this.activeTab      = tab;
    this.error          = '';
    this.selectedUpiApp = '';
    this.upiInputMode   = false;
    this.upiForm.reset();
  }

  selectUpiApp(appId: string): void {
    if (this.selectedUpiApp === appId) {
      this.selectedUpiApp = '';
    } else {
      this.selectedUpiApp = appId;
      this.upiInputMode   = false;
      this.upiForm.reset();
    }
  }

  enableUpiInput(): void {
    this.upiInputMode   = true;
    this.selectedUpiApp = '';
  }

  formatCard(event: Event): void {
    const input  = event.target as HTMLInputElement;
    const digits = input.value.replace(/\D/g, '').substring(0, 16);
    input.value  = digits.replace(/(.{4})/g, '$1 ').trim();
    this.cardForm.patchValue({ cardNumber: digits });
  }

  formatExpiry(event: Event): void {
    const input = event.target as HTMLInputElement;
    let val     = input.value.replace(/\D/g, '').substring(0, 4);
    if (val.length >= 3) val = val.substring(0, 2) + '/' + val.substring(2);
    input.value = val;
    this.cardForm.patchValue({ expiry: val });
  }

  getPaymentMode(): string {
    if (this.activeTab === 'card')       return 'CARD';
    if (this.activeTab === 'upi')        return 'UPI';
    return 'NETBANKING';
  }

  canPay(): boolean {
    if (this.activeTab === 'card') return this.cardForm.valid;
    if (this.activeTab === 'upi')  return !!this.selectedUpiApp || (this.upiInputMode && this.upiForm.valid);
    if (this.activeTab === 'netbanking') return !!this.selectedBank;
    return false;
  }

  paymentSuccess = false;
  successDetails: any = null;

  onPayClick(): void {
    this.showConfirm = true;
  }

  confirmPay(): void {
    this.showConfirm = false;
    this.loading     = true;
    this.error       = '';

    const cleanMobile = this.mobileNumber.replace(/\D/g, '').slice(-10);

    const rechargeRequest = {
      userId:       this.userId,
      operatorId:   this.operatorId,
      planId:       this.planId,
      mobileNumber: cleanMobile,
      paymentMode:  this.getPaymentMode()
    };

    this.rechargeService.processRecharge(rechargeRequest).subscribe({
      next: (res) => {
        this.loading = false;
        if (res.status === 'SUCCESS') {
          this.paymentSuccess = true;
          this.successDetails = {
            ...res,
            planName: this.plan?.name,
            mobileNumber: this.mobileNumber
          };
          this.toast.success('Recharge successful!');
        } else {
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

  cancelPay(): void {
    this.showConfirm = false;
    this.error = '❌ Payment cancelled. Your money has not been deducted.';
    this.toast.info('Payment cancelled.');

    if (!this.plan || !this.userId) return;

    const request = {
      userId:       this.userId,
      operatorId:   this.operatorId,
      planId:       this.planId,
      mobileNumber: this.mobileNumber,
      paymentMode:  'CANCELLED'
    };

    this.rechargeService.processRecharge(request).subscribe({
      next: () => {},
      error: () => {}
    });
  }

  get cardNumber() { return this.cardForm.get('cardNumber'); }
  get cardHolder() { return this.cardForm.get('cardHolder'); }
  get expiry()     { return this.cardForm.get('expiry'); }
  get cvv()        { return this.cardForm.get('cvv'); }
  get upiId()      { return this.upiForm.get('upiId'); }
}
