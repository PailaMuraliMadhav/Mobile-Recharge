import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { OperatorService } from '../../services/operator.service';
import { AuthService } from '../../services/auth.service';
import { UserService } from '../../services/user.service';
import { OperatorResponse, PlanResponse } from '../../models/operator.model';
import { UserResponse } from '../../models/auth.model';

@Component({
  selector: 'app-recharge',
  templateUrl: './recharge.component.html',
  styleUrls: ['./recharge.component.css']
})
export class RechargeComponent implements OnInit {
  form: FormGroup;
  operators: OperatorResponse[] = [];
  plans: PlanResponse[] = [];
  selectedPlan: PlanResponse | null = null;
  loadingPlans = false;
  loadingOperators = true;
  rechargeType: 'self' | 'friend' = 'self';
  userProfile: UserResponse | null = null;

  constructor(
    private fb: FormBuilder,
    private operatorService: OperatorService,
    private userService: UserService,
    public authService: AuthService,
    private router: Router
  ) {
    this.form = this.fb.group({
      mobileNumber: ['', [Validators.required, Validators.pattern(/^[6-9]\d{9}$/)]],
      operatorId: ['', Validators.required],
      planId: ['', Validators.required]
    });
  }

  ngOnInit(): void {
    this.operatorService.getAllOperators().subscribe({
      next: (ops) => { this.operators = ops.filter(o => o.isActive); this.loadingOperators = false; },
      error: () => { this.loadingOperators = false; }
    });

    this.userService.getProfile().subscribe({
      next: (profile) => {
        this.userProfile = profile;
        if (this.rechargeType === 'self') {
          this.setSelfNumber();
        }
      }
    });

    this.form.get('operatorId')?.valueChanges.subscribe(id => {
      if (id) { this.loadPlans(id); this.form.patchValue({ planId: '' }); this.selectedPlan = null; }
    });
  }

  setRechargeType(type: 'self' | 'friend'): void {
    this.rechargeType = type;
    if (type === 'self') {
      this.setSelfNumber();
    } else {
      this.form.get('mobileNumber')?.enable();
      this.form.patchValue({ mobileNumber: '' });
    }
  }

  setSelfNumber(): void {
    if (this.userProfile?.phoneNumber) {
      this.form.patchValue({ mobileNumber: this.userProfile.phoneNumber });
      this.form.get('mobileNumber')?.disable();
    }
  }

  loadPlans(operatorId: number): void {
    this.loadingPlans = true;
    this.plans = [];
    this.operatorService.getPlansByOperator(operatorId).subscribe({
      next: (plans) => { this.plans = plans.filter(p => p.isActive); this.loadingPlans = false; },
      error: () => { this.loadingPlans = false; }
    });
  }

  selectPlan(plan: PlanResponse): void {
    this.selectedPlan = plan;
    this.form.patchValue({ planId: plan.id });
  }

  // Navigate to checkout page with all details as state
  onProceed(): void {
    const rawValue = this.form.getRawValue();
    if (this.form.invalid || !this.selectedPlan) { this.form.markAllAsTouched(); return; }
    const userId = this.authService.currentUser?.userId;
    if (!userId) return;

    this.router.navigate(['/checkout'], {
      state: {
        mobileNumber: rawValue.mobileNumber,
        operatorId: Number(rawValue.operatorId),
        planId: Number(this.form.value.planId),
        plan: this.selectedPlan,
        userId
      }
    });
  }

  selectOperator(id: number): void {
    this.form.patchValue({ operatorId: id, planId: '' });
    this.selectedPlan = null;
    this.loadPlans(id);
  }

  get mobileNumber() { return this.form.get('mobileNumber'); }
  get operatorId()   { return this.form.get('operatorId'); }
}
