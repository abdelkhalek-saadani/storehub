import { signalStore, withState, withComputed, withMethods, patchState } from '@ngrx/signals';
import { rxMethod } from '@ngrx/signals/rxjs-interop';
import { computed, inject } from '@angular/core';
import { pipe, switchMap, tap, debounceTime } from 'rxjs';
import { CartItemResponse, CartResponse } from './model/cart-response';
import { OrderApi } from '@shared/service/order-api';
import { UpdateCartItem } from './model/update-cart-request';
import { tapResponse } from '@ngrx/operators';
import { CartItem } from './cart-item/cart-item';

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

  withMethods((store, api = inject(OrderApi)) => {
    let previousItems: CartItemResponse[] = [];
    return {
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
          tap((items) => {
            if (!store.loading()) {
              previousItems = store.items(); // snapshot at the start of a burst
            }
            patchState(store, {
              loading: true,
              error: null,
              items: applyLocalUpsert(store.items(), items),
            });
          }),
          debounceTime(300),
          switchMap((items) =>
            api.upsertItems(items).pipe(
              tapResponse({
                next: (res) => patchState(store, { ...mapResponse(res), loading: false }),
                error: () =>
                  patchState(store, {
                    loading: false,
                    error: 'Failed to update cart',
                    items: previousItems,
                  }),
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
    };
  }),
);

function applyLocalUpsert(items: CartItemResponse[], newItems: UpdateCartItem[]) {
  let existent: CartItemResponse | undefined;
  let itemsToAdd: CartItemResponse[] = [];
  for (const newItem of newItems) {
    existent = items.find((i) => i.productId === newItem.productId);
    if (existent) {
      existent.quantity = newItem.quantity;
      itemsToAdd.push(existent);
      items = items.filter((i) => i.productId != newItem.productId);
    } else {
      itemsToAdd.push(buildCartItemResponse(newItem.productId, newItem.quantity));
    }
  }

  return items.concat(itemsToAdd);
}

function buildCartItemResponse(productId: string, quantity: number): CartItemResponse {
  return {
    itemId: Math.floor(Math.random() * 1000).toString(),
    productId: productId,
    quantity: quantity,
    productName: '',
    productImageUrl: '',
    unitPrice: 0,
    originalLineTotal: 0,
    appliedOfferLabel: '',
    discountAmount: 0,
    finalLineTotal: 0,
  };
}

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
