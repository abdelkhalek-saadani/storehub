import { TestBed, fakeAsync, tick } from '@angular/core/testing';
import { of, throwError } from 'rxjs';
import { CartStore } from './cart-store';
import { OrderApi } from '@shared/service/order-api';
import { CartItemResponse, CartResponse } from './model/cart-response';
import { provideZonelessChangeDetection } from '@angular/core';

function createMockCartItem(i: number): CartItemResponse {
  return {
    productId: `p${i}`,
    quantity: 2,
    itemId: `it${i}`,
    productName: `p-name-${i}`,
    discountAmount: 0,
    finalLineTotal: 20,
    originalLineTotal: 20,
    unitPrice: 10,
    productImageUrl: '',
    appliedOfferLabel: '',
  };
}

const mockCartResponse: CartResponse = {
  cartId: 'cart-1',
  storeId: 'store-1',
  items: [createMockCartItem(1)],
  originalTotal: 100,
  finalTotal: 90,
  totalDiscount: 10,
};

describe('CartStore', () => {
  let orderApiSpy: jasmine.SpyObj<OrderApi>;

  beforeEach(() => {
    orderApiSpy = jasmine.createSpyObj('OrderApi', ['getCart', 'upsertItems', 'clearCart']);

    TestBed.configureTestingModule({
      providers: [provideZonelessChangeDetection(), { provide: OrderApi, useValue: orderApiSpy }],
    });
  });

  function setup() {
    return TestBed.inject(CartStore);
  }

  it('starts with empty initial state', () => {
    const store = setup();
    expect(store.isEmpty()).toBe(true);
    expect(store.itemCount()).toBe(0);
  });

  it('loadCart populates state on success', () => {
    orderApiSpy.getCart.and.returnValue(of(mockCartResponse));
    const store = setup();

    store.loadCart();

    expect(store.cartId()).toBe('cart-1');
    expect(store.itemCount()).toBe(2);
    expect(store.loading()).toBe(false);
    expect(store.error()).toBeNull();
  });

  it('loadCart sets error on failure', () => {
    orderApiSpy.getCart.and.returnValue(throwError(() => new Error('boom')));
    const store = setup();

    store.loadCart();

    expect(store.error()).toBe('Failed to load cart');
    expect(store.loading()).toBe(false);
  });

  it('upsertItems debounces and updates state', (done) => {
    orderApiSpy.upsertItems.and.returnValue(of(mockCartResponse));
    const store = setup();

    store.upsertItems([{ productId: 'p1', quantity: 2 } as any]);
    expect(orderApiSpy.upsertItems).not.toHaveBeenCalled();

    setTimeout(() => {
      expect(orderApiSpy.upsertItems).toHaveBeenCalledTimes(1);
      expect(store.finalTotal()).toBe(90);
      expect(store.cartId()).toBe('cart-1');
      expect(store.itemCount()).toBe(2);
      done();
    }, 350);
  });

  it('clearCart resets to server response', () => {
    orderApiSpy.clearCart.and.returnValue(of({ ...mockCartResponse, items: [], itemCount: 0 }));
    const store = setup();

    store.clearCart();

    expect(store.isEmpty()).toBe(true);
  });
});
