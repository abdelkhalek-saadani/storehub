import { HttpInterceptorFn } from '@angular/common/http';
import { from, switchMap } from 'rxjs';
import { keycloak } from '@core/keycloak';

export const keycloakInterceptor: HttpInterceptorFn = (req, next) => {
  if (!req.url.startsWith('/api')) return next(req); // only your API
  return from(keycloak.updateToken(30)).pipe(
    switchMap(() =>
      next(req.clone({ setHeaders: { Authorization: `Bearer ${keycloak.token}` } }))
    )
  );
};
