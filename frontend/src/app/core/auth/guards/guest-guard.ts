import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { combineLatest, filter, map, take } from 'rxjs';
import { Auth } from '../services/auth';

export const guestGuard: CanActivateFn = () => {
  const auth = inject(Auth);
  const router = inject(Router);

  return combineLatest([auth.sessionChecked$, auth.currentUser$]).pipe(
    filter(([sessionChecked]) => sessionChecked),
    take(1),
    map(([, currentUser]) =>
      currentUser?.authenticated === true ? router.createUrlTree(['/employees']) : true,
    ),
  );
};
