export interface RechargeRequest {
  userId: number;
  operatorId: number;
  planId: number;
  mobileNumber: string;
  paymentMode: string;
}

export interface RechargeResponse {
  id: number;
  userId: number;
  operatorId: number;
  planId: number;
  mobileNumber: string;
  amount: number;
  status: 'PENDING' | 'SUCCESS' | 'FAILED';
  paymentMode: string;
  transactionId: string;
  createdAt: string;
}
