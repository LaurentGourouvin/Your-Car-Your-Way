import { Routes } from '@angular/router';
import { authGuard, supportGuard } from './core/guards/auth.guard';

export const routes: Routes = [
  { path: '', redirectTo: 'login', pathMatch: 'full' },
  {
    path: 'login',
    loadComponent: () =>
      import('./features/auth/login/login.component').then((r) => r.LoginComponent),
  },
  {
    path: 'register',
    loadComponent: () =>
      import('./features/auth/register/register.component').then((r) => r.RegisterComponent),
  },
  {
    path: 'chat',
    canActivate: [authGuard],
    loadComponent: () =>
      import('./features/chat/conversation/conversation.component').then(
        (r) => r.ConversationComponent,
      ),
  },
  {
    path: 'queue',
    canActivate: [authGuard, supportGuard],
    loadComponent: () =>
      import('./features/chat/queue/queue.component').then((r) => r.QueueComponent),
  },
];