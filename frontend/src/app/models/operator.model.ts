export interface OperatorResponse {
  id: number;
  name: string;
  code: string;
  description: string;
  logoUrl?: string;
  isActive: boolean;
  createdAt: string;
}

export interface PlanResponse {
  id: number;
  name: string;
  price: number;
  validityDays: number;
  data: string;
  description: string;
  isActive: boolean;
  operatorId: number;
  operatorName: string;
  isSuggested: boolean;
  createdAt: string;
}

export interface OperatorRequest {
  name: string;
  code: string;
  description: string;
  logoUrl?: string;
}

export interface PlanRequest {
  name: string;
  price: number;
  validityDays: number;
  data: string;
  description: string;
  isSuggested: boolean;
}
