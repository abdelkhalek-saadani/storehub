import {
  ActivatedRouteSnapshot,
  CanActivateFn,
  Router,
  RouterStateSnapshot,
  UrlTree,
} from '@angular/router';
import { AuthGuardData, createAuthGuard } from 'keycloak-angular';
import { inject } from '@angular/core';

// Usage in routes: data: { role: 'EMPLOYEE' }
const hasRequiredRole = async (
  route: any,
  _state: unknown,
  authData: AuthGuardData,
): Promise<boolean | UrlTree> => {
  const { authenticated, grantedRoles } = authData;
  const router = inject(Router);

  if (!authenticated) {
    return router.parseUrl('/welcome');
  }

  const requiredRole: string | undefined = route.data?.['role'];
  if (!requiredRole) {
    // No role specified on the route -> treat as auth-only
    return true;
  }

  const realmHasRole = grantedRoles.realmRoles.includes(requiredRole);
  // If needed to check for resource roles
  const resourceHasRole = Object.values(grantedRoles.resourceRoles).some((roles) =>
    roles.includes(requiredRole),
  );

  if (realmHasRole || resourceHasRole) {
    return true;
  }

  return router.parseUrl('/forbidden');
};

export const roleGuard = createAuthGuard<CanActivateFn>(hasRequiredRole);
