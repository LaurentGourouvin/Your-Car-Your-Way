import { Routes } from '@angular/router';

export const routes: Routes = [
  { path: '', redirectTo: 'login', pathMatch: 'full' },
  {
    path: 'login',
    loadComponent: () =>
      import('./features/auth/login/login.component').then((r) => r.LoginComponent),
  },
  {
    path: 'chat',
    loadComponent: () =>
      import('./features/chat/conversation/conversation.component').then(
        (r) => r.ConversationComponent,
      ),
  },
  {
    path: 'queue',
    loadComponent: () =>
      import('./features/chat/queue/queue.component').then((r) => r.QueueComponent),
  },
];
