import {
  ActivatedRouteSnapshot,
  CanActivateFn,
  Router,
  RouterStateSnapshot,
  UrlTree,
} from '@angular/router';
import { AuthGuardData, createAuthGuard } from 'keycloak-angular';
import { inject } from '@angular/core';

const isAuthenticated = async (
  _route: unknown,
  state: RouterStateSnapshot,
  authData: AuthGuardData,
): Promise<boolean | UrlTree> => {
  const { authenticated } = authData;
  if (authenticated) {
    return true;
  }
  const router = inject(Router);
  return router.createUrlTree(['/welcome'], {
    queryParams: { redirectUrl: state.url },
  });
};

export const authGuard = createAuthGuard<CanActivateFn>(isAuthenticated);
