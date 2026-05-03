import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { OperatorRequest, OperatorResponse, PlanRequest, PlanResponse } from '../models/operator.model';
import { environment } from '../../environments/environment';

@Injectable({ providedIn: 'root' })
export class OperatorService {
  private readonly API = environment.apiUrl;

  constructor(private http: HttpClient) {}

  getAllOperators(): Observable<OperatorResponse[]> {
    return this.http.get<OperatorResponse[]>(`${this.API}/api/operators`);
  }

  getOperatorById(id: number): Observable<OperatorResponse> {
    return this.http.get<OperatorResponse>(`${this.API}/api/operators/${id}`);
  }

  getPlansByOperator(operatorId: number): Observable<PlanResponse[]> {
    return this.http.get<PlanResponse[]>(`${this.API}/api/operators/${operatorId}/plans`);
  }

  getPlanById(planId: number): Observable<PlanResponse> {
    return this.http.get<PlanResponse>(`${this.API}/api/operators/plans/${planId}`);
  }

  // Admin
  getAllOperatorsAdmin(): Observable<OperatorResponse[]> {
    return this.http.get<OperatorResponse[]>(`${this.API}/api/admin/operators`);
  }

  addOperator(request: OperatorRequest): Observable<OperatorResponse> {
    return this.http.post<OperatorResponse>(`${this.API}/api/admin/operators`, request);
  }

  updateOperator(id: number, request: OperatorRequest): Observable<OperatorResponse> {
    return this.http.put<OperatorResponse>(`${this.API}/api/admin/operators/${id}`, request);
  }

  deleteOperator(id: number): Observable<string> {
    return this.http.delete(`${this.API}/api/admin/operators/${id}`, { responseType: 'text' });
  }

  addPlan(operatorId: number, request: PlanRequest): Observable<PlanResponse> {
    return this.http.post<PlanResponse>(`${this.API}/api/admin/operators/${operatorId}/plans`, request);
  }

  updatePlan(planId: number, request: PlanRequest): Observable<PlanResponse> {
    return this.http.patch<PlanResponse>(`${this.API}/api/admin/operators/plans/${planId}`, request);
  }

  deletePlan(planId: number): Observable<string> {
    return this.http.delete(`${this.API}/api/admin/operators/plans/${planId}`, { responseType: 'text' });
  }
}
