export interface PaymentRequestDto {
  rechargeId: number;
  userId: number;
  amount: number;
  paymentMode: string;
}

export interface PaymentResponseDto {
  message: string;
  transactionId: string;
  status: 'PENDING' | 'SUCCESS' | 'FAILED';
  paymentMode: string;
  amount: number;
}
