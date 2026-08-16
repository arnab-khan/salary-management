import { Routes } from '@angular/router';
import { authGuard } from './core/auth/guards/auth-guard';
import { guestGuard } from './core/auth/guards/guest-guard';

export const routes: Routes = [
  {
    path: 'login',
    canActivate: [guestGuard],
    loadComponent: () =>
      import('./core/auth/pages/login/login').then((component) => component.Login),
  },
  {
    path: 'employees',
    canActivate: [authGuard],
    loadComponent: () =>
      import('./features/employee/pages/employee-page/employee-page').then(
        (component) => component.EmployeePage,
      ),
  },
  { path: '', pathMatch: 'full', redirectTo: 'login' },
];
