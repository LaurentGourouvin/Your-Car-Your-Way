import { Component, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../../core/services/auth.service';
import { RegisterRequest } from '../../../shared/models/auth.model';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [FormsModule, RouterLink],
  templateUrl: './register.component.html',
})
export class RegisterComponent {
  private authService = inject(AuthService);
  private router = inject(Router);

  email = signal('');
  password = signal('');
  firstName = signal('');
  lastName = signal('');
  birthDate = signal('');
  address = signal('');
  error = signal<string | null>(null);
  isLoading = signal(false);

  onSubmit() {
    if (!this.email() || !this.password() || !this.firstName() || !this.lastName()) {
      this.error.set('Veuillez remplir tous les champs obligatoires.');
      return;
    }

    this.isLoading.set(true);
    this.error.set(null);

    const request: RegisterRequest = {
      email: this.email(),
      password: this.password(),
      firstName: this.firstName(),
      lastName: this.lastName(),
      birthDate: this.birthDate() || undefined,
      address: this.address() || undefined,
    };

    this.authService.register(request).subscribe({
      next: (auth) => {
        this.authService.saveAuth(auth);
        this.router.navigate(['/chat']);
      },
      error: (err) => {
        if (err.status === 409) {
          this.error.set('Cette adresse email est déjà utilisée.');
        } else {
          this.error.set('Une erreur est survenue. Veuillez réessayer.');
        }
        this.isLoading.set(false);
      },
    });
  }
}