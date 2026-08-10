import { ResolveFn, Router } from '@angular/router';
import { inject } from '@angular/core';
import { StoreContext } from './service/store-context';

import { of, tap, map, catchError, switchMap } from 'rxjs';
import { StoreApi } from './service/store-api';
import { StorePickerService } from './service/store-picker';

export const storeResolver: ResolveFn<string> = (route, state) => {
  const storeSlug = route.paramMap.get('storeSlug')!;
  const storeContext = inject(StoreContext);
  const storeService = inject(StoreApi);
  const picker = inject(StorePickerService);
  const router = inject(Router);

  // If store didn't change and the cached store has storeId
  if (storeContext.storeSlug() === storeSlug && storeContext.storeId()) {
    return storeContext.storeId()!;
  }

  // If store changed or the cached store has storeId missing
  return storeService.getStoreBySlug(storeSlug).pipe(
    tap((store) => {
      storeContext.setStore(store);
    }),
    map((store) => store.storeId),
    catchError(() =>
      picker.pickStore().pipe(
        tap((store) => {
          storeContext.setStore(store);
          // Navigates to where the user wanted to go originally
          router.navigate([state.url.replace(storeSlug, store.storeSlug)]);
        }),
        map((store) => store.storeId),
      ),
    ),
  );
};
