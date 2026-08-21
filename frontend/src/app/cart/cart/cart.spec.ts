import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideZonelessChangeDetection, signal, WritableSignal } from '@angular/core';
import { BehaviorSubject } from 'rxjs';
import { BreakpointObserver, BreakpointState } from '@angular/cdk/layout';
import { Cart } from './cart';
import { CartStore } from '../cart-store';
import { CartSidenav } from '@shared/service/cart-sidenav';
import { StoreContext } from '../../store/service/store-context';
import { CartItemResponse } from '../model/cart-response';
import { provideRouter, RouterLink } from '@angular/router';

function buildItem(overrides: Partial<CartItemResponse> = {}): CartItemResponse {
  return {
    itemId: 'item-1',
    productId: 'b-prod',
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

describe('Cart', () => {
  let component: Cart;
  let fixture: ComponentFixture<Cart>;
  let storeMock: {
    items: WritableSignal<CartItemResponse[]>;
    itemCount: WritableSignal<number>;
    finalTotal: WritableSignal<number>;
    originalTotal: WritableSignal<number>;
    loadCart: jasmine.Spy;
    upsertItems: jasmine.Spy;
  };
  let cartSidenavSpy: jasmine.SpyObj<CartSidenav>;
  let breakpointSubject: BehaviorSubject<BreakpointState>;

  beforeEach(() => {
    storeMock = {
      items: signal<CartItemResponse[]>([]),
      itemCount: signal(0),
      finalTotal: signal(0),
      originalTotal: signal(0),
      loadCart: jasmine.createSpy('loadCart'),
      upsertItems: jasmine.createSpy('upsertItems'),
    };
    cartSidenavSpy = jasmine.createSpyObj('CartSidenav', ['close']);
    breakpointSubject = new BehaviorSubject<BreakpointState>({ matches: false, breakpoints: {} });

    TestBed.configureTestingModule({
      imports: [Cart],
      providers: [
        provideRouter([]),
        provideZonelessChangeDetection(),
        { provide: CartStore, useValue: storeMock },
        { provide: CartSidenav, useValue: cartSidenavSpy },
        { provide: StoreContext, useValue: { storeSlug: signal('my-store') } },
        {
          provide: BreakpointObserver,
          useValue: { observe: () => breakpointSubject.asObservable() },
        },
      ],
    });

    fixture = TestBed.createComponent(Cart);
    component = fixture.componentInstance;
  });

  it('should create and call loadCart on init', async () => {
    fixture.detectChanges();
    await fixture.whenStable();

    expect(component).toBeTruthy();
    expect(storeMock.loadCart).toHaveBeenCalled();
  });

  describe('sortedItems', () => {
    it('sorts items by productId', async () => {
      storeMock.items.set([buildItem({ productId: 'z-prod' }), buildItem({ productId: 'a-prod' })]);

      await fixture.whenStable();

      const ids = component.sortedItems().map((i) => i.productId);
      expect(ids).toEqual(['a-prod', 'z-prod']);
    });
  });

  describe('isMobile', () => {
    it('reflects BreakpointObserver matches', async () => {
      await fixture.whenStable();
      expect(component.isMobile()).toBeFalse();

      breakpointSubject.next({ matches: true, breakpoints: {} });
      await fixture.whenStable();

      expect(component.isMobile()).toBeTrue();
    });
  });

  describe('template (integration)', () => {
    it('shows the mobile checkout button on mobile', async () => {
      breakpointSubject.next({ matches: true, breakpoints: {} });
      await fixture.whenStable();

      const mobileButton = fixture.nativeElement.querySelector('button[matButton="elevated"]');
      const desktopButton = fixture.nativeElement.querySelector('button[matButton="filled"]');
      expect(desktopButton).toBeFalsy();
      expect(mobileButton).toBeTruthy();
    });

    it('shows the desktop checkout button on desktop', async () => {
      breakpointSubject.next({ matches: false, breakpoints: {} });
      await fixture.whenStable();

      const desktopButton = fixture.nativeElement.querySelector('button[matButton="filled"]');
      const mobileButton = fixture.nativeElement.querySelector('button[matButton="elevated"]');
      expect(desktopButton).toBeTruthy();
      expect(mobileButton).toBeFalsy();
    });

    it('calls cartSidenavService.close() when the close button is clicked', async () => {
      fixture.detectChanges();
      await fixture.whenStable();

      const closeBtn: HTMLButtonElement = fixture.nativeElement.querySelector('.btn-close');
      closeBtn.click();
      await fixture.whenStable();

      expect(cartSidenavSpy.close).toHaveBeenCalled();
    });

    it('shows the item count badge from store.itemCount()', async () => {
      storeMock.itemCount.set(5);
      await fixture.whenStable();

      const badge = fixture.nativeElement.querySelector('.rounded-full span');
      expect(badge.textContent.trim()).toBe('5');
    });

    it('shows the strikethrough original total only when it differs from the final total', async () => {
      storeMock.finalTotal.set(10);
      storeMock.originalTotal.set(10);
      await fixture.whenStable();
      expect(fixture.nativeElement.querySelector('.line-through')).toBeFalsy();

      storeMock.originalTotal.set(15);
      await fixture.whenStable();
      expect(fixture.nativeElement.querySelector('.line-through')).toBeTruthy();
    });
  });
});
