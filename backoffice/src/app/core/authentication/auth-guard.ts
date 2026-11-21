import { inject } from '@angular/core';
import { ActivatedRouteSnapshot, Router, RouterStateSnapshot } from '@angular/router';
import { AuthService } from './auth.service';
import {hasAnyRole, keycloak} from '@core/keycloak';

export const authGuard = (route: ActivatedRouteSnapshot, state: RouterStateSnapshot) => {

  if (!keycloak.authenticated) {
    keycloak.login({ redirectUri: location.origin + state.url });
    return false;
  }

  const required = route.data?.['roles'] as string[] | undefined;
  if (required?.length && !hasAnyRole(required)) {
    return inject(Router).parseUrl('/403');
  }
  return true;
};
