import { ResolveFn } from '@angular/router';
import { inject } from '@angular/core';
import { StoreContext } from './service/store-context';

import { of, tap, map } from 'rxjs';
import { StoreApi } from '@shared/service/store-api';

export const storeResolver: ResolveFn<string> = (route) => {
  const storeSlug = route.paramMap.get('storeSlug')!;
  const storeContext = inject(StoreContext);
  const storeService = inject(StoreApi);

  if (storeContext.storeSlug() === storeSlug && storeContext.storeId()) {
    return storeContext.storeId()!;
  }

  return storeService.getStoreBySlug(storeSlug).pipe(
    tap((store) => {
      storeContext.setStoreId(store.storeId);
      storeContext.setStoreSlug(storeSlug);
    }),
    map((store) => store.storeId),
  );
};
