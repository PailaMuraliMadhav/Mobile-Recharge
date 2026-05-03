import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { UpdateProfile, UserResponse } from '../models/auth.model';
import { environment } from '../../environments/environment';

@Injectable({ providedIn: 'root' })
export class UserService {
  private readonly API = environment.apiUrl;

  constructor(private http: HttpClient) {}

  getProfile(): Observable<UserResponse> {
    return this.http.get<UserResponse>(`${this.API}/api/users/profile`);
  }

  updateProfile(request: UpdateProfile): Observable<UserResponse> {
    return this.http.patch<UserResponse>(`${this.API}/api/users/profile`, request);
  }

  deleteMyAccount(): Observable<string> {
    return this.http.delete(`${this.API}/api/users/profile`, { responseType: 'text' });
  }

  getRechargeHistory(): Observable<any[]> {
    return this.http.get<any[]>(`${this.API}/api/users/recharge-history`);
  }

  getTransactionStatus(transactionId: string): Observable<any> {
    return this.http.get<any>(`${this.API}/api/users/transaction/${transactionId}`);
  }

  // Admin
  getAllUsers(): Observable<UserResponse[]> {
    return this.http.get<UserResponse[]>(`${this.API}/api/admin/users`);
  }

  getUserById(id: number): Observable<UserResponse> {
    return this.http.get<UserResponse>(`${this.API}/api/admin/users/${id}`);
  }

  blockUser(id: number): Observable<string> {
    return this.http.patch(`${this.API}/api/admin/users/${id}/block`, {}, { responseType: 'text' });
  }

  unblockUser(id: number): Observable<string> {
    return this.http.patch(`${this.API}/api/admin/users/${id}/unblock`, {}, { responseType: 'text' });
  }
}
