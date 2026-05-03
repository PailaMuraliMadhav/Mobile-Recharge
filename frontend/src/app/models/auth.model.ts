export interface LoginRequest {
  email: string;
  password: string;
}

export interface RegisterRequest {
  name: string;
  email: string;
  password: string;
  phoneNumber: string;
}

export interface LoginResponse {
  token: string;
  type: string;
  userId: number;
  email: string;
  role: 'USER' | 'ADMIN';
  expiresIn: number;
}

export interface UserResponse {
  id: number;
  name: string;
  email: string;
  phoneNumber: string;
  role: 'USER' | 'ADMIN';
  isActive: boolean;
  createdAt: string;
}

export interface UpdateProfile {
  name?: string;
  phoneNumber?: string;
}
