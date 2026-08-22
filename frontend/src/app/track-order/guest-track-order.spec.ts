import { Component, provideZonelessChangeDetection } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { of, throwError, Subject } from 'rxjs';
import { ActivatedRoute, convertToParamMap } from '@angular/router';
import GuestTrackOrderPage from './guest-track-order';
import { OrderApi, OrderResponse } from '@shared/service/order-api';
import { OrderDetailView } from './order-details-view/order-details-view';

@Component({
  selector: 'app-order-detail-view',
  template: '',
  standalone: true,
  inputs: ['orderResult'],
})
class OrderDetailViewStub {
  orderResult: unknown;
}

function buildOrderResponse(overrides: Partial<OrderResponse> = {}) {
  return {
    orderId: 'order-1',
    userId: 'user-1',
    storeId: 'store-1',
    originalTotal: 10,
    finalTotal: 10,
    totalDiscount: 0,
    items: [],
    deliveryAddress: '',
    billingAddress: '',
    slotId: '',
    deliveryFee: '',
    status: { code: '', label: '' },
    paymentId: '',
    paymentApprovalLink: '',
    createdAt: '',
    ...overrides,
  };
}

describe('GuestTrackOrderPage', () => {
  let component: GuestTrackOrderPage;
  let fixture: ComponentFixture<GuestTrackOrderPage>;
  let orderApiSpy: jasmine.SpyObj<OrderApi>;

  async function setup(
    options: { queryParams?: Record<string, string>; getGuestOrder$?: any } = {},
  ) {
    orderApiSpy = jasmine.createSpyObj('OrderApi', ['getGuestOrder']);
    orderApiSpy.getGuestOrder.and.returnValue(options.getGuestOrder$ ?? of(buildOrderResponse()));

    TestBed.configureTestingModule({
      imports: [GuestTrackOrderPage],
      providers: [
        provideZonelessChangeDetection(),
        { provide: OrderApi, useValue: orderApiSpy },
        {
          provide: ActivatedRoute,
          useValue: { snapshot: { queryParamMap: convertToParamMap(options.queryParams ?? {}) } },
        },
      ],
    });

    TestBed.overrideComponent(GuestTrackOrderPage, {
      remove: { imports: [OrderDetailView] },
      add: { imports: [OrderDetailViewStub] },
    });

    fixture = TestBed.createComponent(GuestTrackOrderPage);
    component = fixture.componentInstance;
    await fixture.whenStable();
  }

  it('should create', async () => {
    await setup();
    expect(component).toBeTruthy();
  });

  describe('ngOnInit', () => {
    it('pre-fills orderId from the query param when present', async () => {
      await setup({ queryParams: { orderId: 'abc-123' } });

      expect(component.form.controls.orderId.value).toBe('abc-123');
    });

    it('leaves orderId empty when the query param is absent', async () => {
      await setup({});

      expect(component.form.controls.orderId.value).toBe('');
    });
  });

  describe('lookup', () => {
    it('marks all fields touched and does not set searchParams when the form is invalid', async () => {
      await setup({});
      spyOn(component.form, 'markAllAsTouched').and.callThrough();

      component.lookup();

      expect(component.form.markAllAsTouched).toHaveBeenCalled();
      expect(component.searchParams()).toBeUndefined();
    });

    it('sets searchParams from the raw form value when valid, triggering a fetch', async () => {
      await setup({});

      component.form.setValue({ orderId: 'order-1', email: 'test@example.com' });
      component.lookup();
      await fixture.whenStable();

      expect(component.searchParams()).toEqual({ orderId: 'order-1', email: 'test@example.com' });
      expect(orderApiSpy.getGuestOrder).toHaveBeenCalledWith('order-1', 'test@example.com');
    });
  });

  describe('isTouched / isInvalid', () => {
    it('reflect control state', async () => {
      await setup({});

      expect(component.isTouched('email')).toBeFalse();
      expect(component.isInvalid('email')).toBeTrue(); // required, empty by default

      component.form.controls.email.markAsTouched();
      expect(component.isTouched('email')).toBeTrue();
    });
  });

  describe('template (integration)', () => {
    it('shows "No matching order found" when orderResult errors', async () => {
      await setup({ getGuestOrder$: throwError(() => new Error('not found')) });

      component.form.setValue({ orderId: 'bad-id', email: 'test@example.com' });
      component.lookup();
      await fixture.whenStable();

      expect(fixture.nativeElement.textContent).toContain('No matching order found');
    });

    it('disables the submit button and shows "Searching..." while the resource is loading', async () => {
      const subject = new Subject<any>();
      await setup({ getGuestOrder$: subject.asObservable() });

      component.form.setValue({ orderId: 'order-1', email: 'test@example.com' });
      component.lookup();

      fixture.detectChanges(); // to force a sync render and see the loading state

      const button: HTMLButtonElement =
        fixture.nativeElement.querySelector('button[type="submit"]');
      expect(button.disabled).toBeTrue();
      expect(button.textContent).toContain('Searching...');

      subject.next(buildOrderResponse());
      subject.complete();
      await fixture.whenStable();
      expect(button.disabled).toBeFalse();
      expect(button.textContent).toContain('Find');
    });
  });
});
