import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { PaymentRequestDto, PaymentResponseDto } from '../models/payment.model';
import { environment } from '../../environments/environment';

@Injectable({ providedIn: 'root' })
export class PaymentService {
  private readonly API = environment.apiUrl;

  constructor(private http: HttpClient) {}

  processPayment(request: PaymentRequestDto): Observable<PaymentResponseDto> {
    return this.http.post<PaymentResponseDto>(`${this.API}/api/payments/process`, request);
  }

  getTransactionStatus(transactionId: string): Observable<PaymentResponseDto> {
    return this.http.get<PaymentResponseDto>(`${this.API}/api/payments/${transactionId}`);
  }

  getPaymentHistory(userId: number): Observable<PaymentResponseDto[]> {
    return this.http.get<PaymentResponseDto[]>(`${this.API}/api/payments/user/${userId}`);
  }
  createRazorpayOrder(request: any): Observable<any> {
    return this.http.post<any>(`${this.API}/api/payments/razorpay/create-order`, request);
  }

  verifyRazorpayPayment(request: any): Observable<PaymentResponseDto> {
    return this.http.post<PaymentResponseDto>(`${this.API}/api/payments/razorpay/verify`, request);
  }
}
