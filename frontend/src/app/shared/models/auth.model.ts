export interface LoginRequest {
  email: string;
  password: string;
}

export interface RegisterRequest {
  email: string;
  password: string;
  firstName: string;
  lastName: string;
  birthDate?: string;
  address?: string;
}

export interface AuthResponse {
  token: string;
  userId: string;
  email: string;
  roles: string[];
}
