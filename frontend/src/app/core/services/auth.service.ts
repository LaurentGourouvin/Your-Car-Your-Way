import { Injectable, inject, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { AuthResponse, LoginRequest, RegisterRequest } from '../../shared/models/auth.model';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private http = inject(HttpClient);
  private readonly API_URL = 'http://localhost:8080/api/auth';

  currentUser = signal<AuthResponse | null>(null);

  constructor() {
    const stored = localStorage.getItem('auth');
    if (stored) {
      this.currentUser.set(JSON.parse(stored));
    }
  }

  login(request: LoginRequest) {
    return this.http.post<AuthResponse>(`${this.API_URL}/login`, request);
  }

  register(request: RegisterRequest) {
    return this.http.post<AuthResponse>(`${this.API_URL}/register`, request);
  }

  saveAuth(auth: AuthResponse) {
    localStorage.setItem('auth', JSON.stringify(auth));
    this.currentUser.set(auth);
  }

  logout() {
    localStorage.removeItem('auth');
    this.currentUser.set(null);
  }

  getToken(): string | null {
    return this.currentUser()?.token ?? null;
  }

  isLoggedIn(): boolean {
    return this.currentUser() !== null;
  }

  isSupport(): boolean {
    return this.currentUser()?.roles.includes('SUPPORT') ?? false;
  }
}
