import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { RechargeRequest, RechargeResponse } from '../models/recharge.model';
import { AdminAnalytics } from '../models/analytics.model';
import { environment } from '../../environments/environment';

@Injectable({ providedIn: 'root' })
export class RechargeService {
  private readonly API = environment.apiUrl;

  constructor(private http: HttpClient) {}

  processRecharge(request: RechargeRequest): Observable<RechargeResponse> {
    return this.http.post<RechargeResponse>(`${this.API}/api/recharges`, request);
  }



  getRechargeHistory(userId: number): Observable<RechargeResponse[]> {
    return this.http.get<RechargeResponse[]>(`${this.API}/api/recharges/user/${userId}`);
  }

  getAllRecharges(): Observable<RechargeResponse[]> {
    return this.http.get<RechargeResponse[]>(`${this.API}/api/recharges/admin/all`);
  }

  getAnalytics(): Observable<AdminAnalytics> {
    return this.http.get<AdminAnalytics>(`${this.API}/api/recharges/admin/analytics`);
  }
}
