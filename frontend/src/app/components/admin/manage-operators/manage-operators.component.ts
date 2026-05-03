import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { OperatorService } from '../../../services/operator.service';
import { OperatorResponse, PlanResponse } from '../../../models/operator.model';
import { ToastService } from '../../../services/toast.service';

@Component({
  selector: 'app-manage-operators',
  templateUrl: './manage-operators.component.html',
  styleUrls: ['./manage-operators.component.css']
})
export class ManageOperatorsComponent implements OnInit {
  operators: OperatorResponse[] = [];
  selectedOperator: OperatorResponse | null = null;
  plans: PlanResponse[] = [];
  loading = true;
  loadingPlans = false;

  // Operator form
  operatorForm: FormGroup;
  showOperatorForm = false;
  editingOperator: OperatorResponse | null = null;
  savingOperator = false;

  // Plan form
  planForm: FormGroup;
  showPlanForm = false;
  editingPlan: PlanResponse | null = null;
  savingPlan = false;

  constructor(
    private operatorService: OperatorService,
    private fb: FormBuilder,
    private toast: ToastService
  ) {
    this.operatorForm = this.fb.group({
      name:        ['', [Validators.required, Validators.minLength(2)]],
      code:        ['', [Validators.required, Validators.minLength(2), Validators.maxLength(10)]],
      description: [''],
      logoUrl:     ['']
    });

    this.planForm = this.fb.group({
      name:         ['', [Validators.required, Validators.minLength(2)]],
      price:        ['', [Validators.required, Validators.min(1)]],
      validityDays: ['', [Validators.required, Validators.min(1)]],
      data:         ['', Validators.required],
      description:  [''],
      isSuggested:  [false]
    });
  }

  ngOnInit(): void {
    this.loadOperators();
  }

  loadOperators(): void {
    this.operatorService.getAllOperatorsAdmin().subscribe({
      next: (ops) => {
        this.operators = ops;
        this.loading = false;
        if (ops.length > 0 && !this.selectedOperator) {
          this.selectOperator(ops[0]);
        }
      },
      error: () => { this.loading = false; }
    });
  }

  selectOperator(op: OperatorResponse): void {
    this.selectedOperator = op;
    this.loadingPlans = true;
    this.plans = [];
    this.operatorService.getPlansByOperator(op.id).subscribe({
      next: (plans) => { this.plans = plans; this.loadingPlans = false; },
      error: () => { this.loadingPlans = false; }
    });
  }

  // Operator CRUD
  openAddOperator(): void {
    this.editingOperator = null;
    this.operatorForm.reset();
    this.showOperatorForm = true;
  }

  openEditOperator(op: OperatorResponse): void {
    this.editingOperator = op;
    this.operatorForm.patchValue({
      name: op.name, code: op.code,
      description: op.description, logoUrl: op.logoUrl ?? ''
    });
    this.showOperatorForm = true;
  }

  closeOperatorForm(): void {
    this.showOperatorForm = false;
    this.editingOperator = null;
    this.operatorForm.reset();
  }

  saveOperator(): void {
    if (this.operatorForm.invalid) { this.operatorForm.markAllAsTouched(); return; }
    this.savingOperator = true;
    const data = this.operatorForm.value;
    const isEditing = !!this.editingOperator;

    const obs = isEditing
      ? this.operatorService.updateOperator(this.editingOperator!.id, data)
      : this.operatorService.addOperator(data);

    obs.subscribe({
      next: () => {
        this.savingOperator = false;
        this.closeOperatorForm();
        this.loadOperators();
        this.toast.success(isEditing ? 'Operator updated successfully!' : 'Operator added successfully!');
      },
      error: (err) => {
        this.savingOperator = false;
        this.toast.error(err.error?.message || 'Operation failed');
      }
    });
  }

  deleteOperator(id: number): void {
    if (!confirm('Delete this operator? This will also delete all its plans.')) return;
    this.operatorService.deleteOperator(id).subscribe({
      next: () => {
        this.operators = this.operators.filter(o => o.id !== id);
        if (this.selectedOperator?.id === id) {
          this.selectedOperator = null;
          this.plans = [];
        }
        this.toast.success('Operator deleted.');
      },
      error: (err) => this.toast.error(err.error?.message || 'Delete failed')
    });
  }

  // Plan CRUD
  openAddPlan(): void {
    this.editingPlan = null;
    this.planForm.reset();
    this.showPlanForm = true;
  }

  openEditPlan(plan: PlanResponse): void {
    this.editingPlan = plan;
    this.planForm.patchValue({
      name: plan.name, price: plan.price,
      validityDays: plan.validityDays, data: plan.data, 
      description: plan.description, isSuggested: plan.isSuggested
    });
    this.showPlanForm = true;
  }

  closePlanForm(): void {
    this.showPlanForm = false;
    this.editingPlan = null;
    this.planForm.reset();
  }

  savePlan(): void {
    if (this.planForm.invalid) { this.planForm.markAllAsTouched(); return; }
    if (!this.selectedOperator) return;
    this.savingPlan = true;
    const data = this.planForm.value;
    const isEditingPlan = !!this.editingPlan;

    const obs = isEditingPlan
      ? this.operatorService.updatePlan(this.editingPlan!.id, data)
      : this.operatorService.addPlan(this.selectedOperator.id, data);

    obs.subscribe({
      next: () => {
        this.savingPlan = false;
        this.closePlanForm();
        this.selectOperator(this.selectedOperator!);
        this.toast.success(isEditingPlan ? 'Plan updated successfully!' : 'Plan added successfully!');
      },
      error: (err) => {
        this.savingPlan = false;
        this.toast.error(err.error?.message || 'Operation failed');
      }
    });
  }

  deletePlan(planId: number): void {
    if (!confirm('Delete this plan?')) return;
    this.operatorService.deletePlan(planId).subscribe({
      next: () => {
        this.plans = this.plans.filter(p => p.id !== planId);
        this.toast.success('Plan deleted.');
      },
      error: (err) => this.toast.error(err.error?.message || 'Delete failed')
    });
  }
}
