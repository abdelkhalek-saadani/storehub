import { Component, provideZonelessChangeDetection, signal, WritableSignal } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { BehaviorSubject } from 'rxjs';
import { BreakpointObserver, BreakpointState } from '@angular/cdk/layout';
import { CartItemResponse } from '../cart/model/cart-response';
import CheckoutPage from './checkout';
import { CartStore } from '../cart/cart-store';
import { ReviewOrder } from '@shared/components/review-order/review-order';
import { CheckoutDetails } from './checkout-details/checkout-details';
import { CheckoutForm } from './checkout-form/checkout-form';

@Component({ selector: 'app-review-order', template: '', standalone: true, inputs: ['items'] })
class ReviewOrderStub {
  items: unknown;
}

@Component({ selector: 'app-checkout-details', template: '', standalone: true })
class CheckoutDetailsStub {}

@Component({ selector: 'app-checkout-form', template: '', standalone: true })
class CheckoutFormStub {}

function buildItem(overrides: Partial<CartItemResponse> = {}): CartItemResponse {
  return {
    itemId: 'item-1',
    productId: 'prod-1',
    productName: 'Milk',
    productImageUrl: '',
    quantity: 1,
    unitPrice: 3.5,
    originalLineTotal: 3.5,
    discountAmount: 0,
    finalLineTotal: 3.5,
    appliedOfferLabel: '',
    ...overrides,
  };
}

describe('CheckoutPage', () => {
  let component: CheckoutPage;
  let fixture: ComponentFixture<CheckoutPage>;
  let storeMock: { items: WritableSignal<CartItemResponse[]> };
  let breakpointSubject: BehaviorSubject<BreakpointState>;

  beforeEach(async () => {
    storeMock = { items: signal<CartItemResponse[]>([]) };
    breakpointSubject = new BehaviorSubject<BreakpointState>({ matches: false, breakpoints: {} });

    TestBed.configureTestingModule({
      imports: [CheckoutPage],
      providers: [
        provideZonelessChangeDetection(),
        { provide: CartStore, useValue: storeMock },
        {
          provide: BreakpointObserver,
          useValue: { observe: () => breakpointSubject.asObservable() },
        },
      ],
    });

    TestBed.overrideComponent(CheckoutPage, {
      remove: { imports: [ReviewOrder, CheckoutDetails, CheckoutForm] },
      add: { imports: [ReviewOrderStub, CheckoutDetailsStub, CheckoutFormStub] },
    });

    fixture = TestBed.createComponent(CheckoutPage);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', async () => {
    expect(component).toBeTruthy();
  });

  describe('cartItems', () => {
    it('mirrors cartStore.items()', async () => {
      const items = [
        buildItem({ productId: 'a' }),
        buildItem({ itemId: 'item-2', productId: 'b' }),
      ];
      storeMock.items.set(items);
      await fixture.whenStable();

      expect(component.cartItems()).toEqual(items);
    });
  });

  describe('isMobile', () => {
    it('reflects BreakpointObserver matches', async () => {
      expect(component.isMobile()).toBeFalse();

      breakpointSubject.next({ matches: true, breakpoints: {} });
      await fixture.whenStable();

      expect(component.isMobile()).toBeTrue();
    });
  });

  describe('template', () => {
    it('renders the checkout form, review order, and checkout details sections', async () => {
      expect(fixture.nativeElement.querySelector('app-checkout-form')).toBeTruthy();
      expect(fixture.nativeElement.querySelector('app-review-order')).toBeTruthy();
      expect(fixture.nativeElement.querySelector('app-checkout-details')).toBeTruthy();
    });
  });
});
