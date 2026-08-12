import { CanActivateFn, Router, UrlTree } from '@angular/router';
import { inject } from '@angular/core';
import { AuthGuardData, createAuthGuard } from 'keycloak-angular';

const redirectIfAuthenticated = async (
  _route: unknown,
  _state: unknown,
  authData: AuthGuardData,
): Promise<boolean | UrlTree> => {
  const { authenticated } = authData;
  const router = inject(Router);

  if (authenticated) {
    return router.parseUrl('/');
  }
  return true;
};

export const guestOnlyGuard = createAuthGuard<CanActivateFn>(redirectIfAuthenticated);
