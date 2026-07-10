import { ResolveFn } from '@angular/router';
import { inject } from '@angular/core';
import { StoreContext } from './service/store-context';

export const storeResolver: ResolveFn<string> = (route) => {
  const storeId = route.paramMap.get('storeId')!;
  inject(StoreContext).setStoreId(storeId);
  return storeId;
};
