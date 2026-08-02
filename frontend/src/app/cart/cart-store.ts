import { signalStore, withState, withComputed, withMethods, patchState } from '@ngrx/signals';
import { rxMethod } from '@ngrx/signals/rxjs-interop';
import { computed, inject } from '@angular/core';
import { pipe, switchMap, tap, debounceTime } from 'rxjs';
import { CartResponse } from './model/cart-response';
import { OrderApi } from '@shared/service/order-api';
import { UpdateCartItem } from './model/update-cart-request';
import { tapResponse } from '@ngrx/operators';

interface CartState {
  cartId: string | null;
  storeId: string | null;
  items: CartResponse['items'];
  originalTotal: number;
  finalTotal: number;
  totalDiscount: number;
  loading: boolean;
  error: string | null;
}

const initialState: CartState = {
  cartId: null,
  storeId: null,
  items: [],
  originalTotal: 0,
  finalTotal: 0,
  totalDiscount: 0,
  loading: false,
  error: null,
};

export const CartStore = signalStore(
  { providedIn: 'root' },
  withState(initialState),

  withComputed(({ items }) => ({
    itemCount: computed(() => items().reduce((sum, i) => sum + i.quantity, 0)),
    isEmpty: computed(() => items().length === 0),
  })),

  withMethods((store, api = inject(OrderApi)) => ({
    loadCart: rxMethod<void>(
      pipe(
        tap(() => patchState(store, { loading: true, error: null })),
        switchMap(() =>
          api.getCart().pipe(
            tapResponse({
              next: (res) => patchState(store, { ...mapResponse(res), loading: false }),
              error: () => patchState(store, { loading: false, error: 'Failed to load cart' }),
            }),
          ),
        ),
      ),
    ),

    upsertItems: rxMethod<UpdateCartItem[]>(
      pipe(
        debounceTime(300),
        tap(() => patchState(store, { loading: true, error: null })),
        switchMap((items) =>
          api.upsertItems(items).pipe(
            tapResponse({
              next: (res) => patchState(store, { ...mapResponse(res), loading: false }),
              error: () => patchState(store, { loading: false, error: 'Failed to update cart' }),
            }),
          ),
        ),
      ),
    ),

    clearCart: rxMethod<void>(
      pipe(
        tap(() => patchState(store, { loading: true, error: null })),
        switchMap(() =>
          api.clearCart().pipe(
            tapResponse({
              next: (res) => patchState(store, { ...mapResponse(res), loading: false }),
              error: () => patchState(store, { loading: false, error: 'Failed to clear cart' }),
            }),
          ),
        ),
      ),
    ),
  })),
);

// This mapper is just in case the backend response diverge from what we except in the store
function mapResponse(res: CartResponse) {
  return {
    cartId: res.cartId,
    storeId: res.storeId,
    items: res.items,
    originalTotal: res.originalTotal,
    finalTotal: res.finalTotal,
    totalDiscount: res.totalDiscount,
  };
}
