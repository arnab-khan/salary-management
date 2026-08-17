import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { filter, map, take } from 'rxjs';
import { Auth } from '../services/auth';

export const authGuard: CanActivateFn = (route, state) => {
  const auth = inject(Auth);
  const router = inject(Router);

  return auth.currentUser$.pipe(
    filter((currentUser) => currentUser !== null),
    take(1),
    map((currentUser) =>
      currentUser?.authenticated === true ? true : router.createUrlTree(['/login']),
    ),
  );
};
