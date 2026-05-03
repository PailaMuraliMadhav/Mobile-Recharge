import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { OperatorService } from '../../services/operator.service';
import { OperatorResponse, PlanResponse } from '../../models/operator.model';

@Component({
  selector: 'app-operators',
  templateUrl: './operators.component.html',
  styleUrls: ['./operators.component.css']
})
export class OperatorsComponent implements OnInit {
  operators: OperatorResponse[] = [];
  selectedOperator: OperatorResponse | null = null;
  plans: PlanResponse[] = [];
  loading = true;
  loadingPlans = false;

  constructor(private operatorService: OperatorService, private router: Router) {}

  ngOnInit(): void {
    this.operatorService.getAllOperators().subscribe({
      next: (ops) => {
        this.operators = ops.filter(o => o.isActive);
        this.loading = false;
        if (this.operators.length > 0) {
          this.selectOperator(this.operators[0]);
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
      next: (plans) => {
        this.plans = plans.filter(p => p.isActive);
        this.loadingPlans = false;
      },
      error: () => { this.loadingPlans = false; }
    });
  }

  rechargeWithPlan(plan: PlanResponse): void {
    this.router.navigate(['/recharge'], {
      queryParams: { operatorId: plan.operatorId, planId: plan.id }
    });
  }
}
