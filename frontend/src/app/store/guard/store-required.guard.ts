import { CanActivateFn, Router } from '@angular/router';
import { inject } from '@angular/core';
import { StoreContext } from '../service/store-context';

export const storeRequiredGuard: CanActivateFn = (_route, state) => {
  const storeContext = inject(StoreContext);
  const router = inject(Router);

  const cached = storeContext.getCurrentStore();
  if (cached?.slug) {
    return router.parseUrl(`/store/${cached.slug}${state.url}`);
  }

  return router.parseUrl(`/welcome-pick-store?returnUrl=${encodeURIComponent(state.url)}`);
};
