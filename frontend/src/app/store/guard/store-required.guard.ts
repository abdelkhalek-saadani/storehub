import { CanActivateFn, Router } from '@angular/router';
import { inject } from '@angular/core';
import { map } from 'rxjs/operators';
import { StoreContext } from '../service/store-context';
import { StorePickerService } from '../service/store-picker';

export const storeRequiredGuard: CanActivateFn = (_route, state) => {
  const storeContext = inject(StoreContext);
  const router = inject(Router);
  const picker = inject(StorePickerService);

  const cached = storeContext.getCurrentStore();
  if (cached?.slug) {
    return router.parseUrl(`/store/${cached.slug}${state.url}`);
  }

  return picker
    .pickStore()
    .pipe(map((store) => router.parseUrl(`/store/${store.storeSlug}${state.url}`)));
};
