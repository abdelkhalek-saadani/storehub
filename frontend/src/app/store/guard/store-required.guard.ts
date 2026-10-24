import { CanActivateFn, Router } from '@angular/router';
import { inject } from '@angular/core';
import { StoreContext } from '../service/store-context';

export const storeRequiredGuard: CanActivateFn = (_route, state) => {
  const storeContext = inject(StoreContext);
  const router = inject(Router);

  // Already under /store/... and still unmatched: real 404, don't loop
  if (state.url.startsWith('/store/')) {
    return router.parseUrl('/not-found');
  }

  const cached = storeContext.getCurrentStore();
  if (cached?.slug) {
    return router.parseUrl(`/store/${cached.slug}${state.url}`);
  }

  return router.parseUrl(`/welcome-pick-store?returnUrl=${encodeURIComponent(state.url)}`);
};
