import { Component, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../../core/services/auth.service';
import { LoginRequest } from '../../../shared/models/auth.model';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [FormsModule],
  templateUrl: './login.component.html',
})
export class LoginComponent {
  private authService = inject(AuthService);
  private router = inject(Router);

  email = signal('');
  password = signal('');
  error = signal<string | null>(null);
  isLoading = signal(false);

  onSubmit() {
    this.isLoading.set(true);
    this.error.set(null);

    const request: LoginRequest = {
      email: this.email(),
      password: this.password(),
    };

    this.authService.login(request).subscribe({
      next: (auth) => {
        this.authService.saveAuth(auth);
        if (auth.roles.includes('SUPPORT')) {
          this.router.navigate(['/queue']);
        } else {
          this.router.navigate(['/chat']);
        }
      },
      error: () => {
        this.error.set('Email ou mot de passe incorrect');
        this.isLoading.set(false);
      },
    });
  }
}
