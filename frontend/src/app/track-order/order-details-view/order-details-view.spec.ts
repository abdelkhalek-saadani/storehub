import { Component, provideZonelessChangeDetection } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { of, throwError, Subject } from 'rxjs';
import { HttpErrorResponse } from '@angular/common/http';
import { BreakpointObserver, BreakpointState } from '@angular/cdk/layout';
import { MatSnackBar } from '@angular/material/snack-bar';
import { LocalDateTime } from '@js-joda/core';
import { OrderDetailView } from './order-details-view';
import { OrderApi, OrderCancelResponse } from '@shared/service/order-api';
import { CatalogApi } from '@shared/service/catalog-api';
import { Toaster } from '@shared/service/toaster';
import { OrderTracking } from '../order-tracking/order-tracking';
import { PaymentSummary } from '../payment-summary/payment-summary';
import { LocationSummary } from '../location-summary/location-summary';
import { OrderSummary } from '../order-summary/order-summary';
import { ReviewOrder } from '@shared/components/review-order/review-order';
import { InvoiceDownload } from '../invoice-download/invoice-download';

// Stub the children so this spec stays isolated to OrderDetailView's own logic.
@Component({
  selector: 'app-order-tracking',
  template: '',
  standalone: true,
  inputs: ['status', 'createdAt', 'orderArriveIn'],
})
class OrderTrackingStub {
  status: unknown;
  createdAt: unknown;
  orderArriveIn: unknown;
}

@Component({ selector: 'app-payment-summary', template: '', standalone: true })
class PaymentSummaryStub {}

@Component({
  selector: 'app-location-summary',
  template: '',
  standalone: true,
  inputs: ['deliveryAddress'],
})
class LocationSummaryStub {
  deliveryAddress: unknown;
}

@Component({
  selector: 'app-order-summary',
  template: '',
  standalone: true,
  inputs: ['orderNumber', 'itemsTotal'],
})
class OrderSummaryStub {
  orderNumber: unknown;
  itemsTotal: unknown;
}

@Component({ selector: 'app-review-order', template: '', standalone: true, inputs: ['items'] })
class ReviewOrderStub {
  items: unknown;
}

@Component({ selector: 'app-invoice-download', template: '', standalone: true })
class InvoiceDownloadStub {}

function buildOrderResponse(overrides: Partial<any> = {}) {
  return {
    orderId: 'order-1',
    userId: 'user-1',
    storeId: 'store-1',
    originalTotal: 10,
    finalTotal: 10,
    totalDiscount: 0,
    items: [],
    deliveryAddress: '123 Main St',
    billingAddress: '',
    slotId: '',
    deliveryFee: '',
    status: { code: 'CREATED', label: 'Created' },
    paymentId: '',
    paymentApprovalLink: '',
    createdAt: '2026-08-20T10:00:00',
    ...overrides,
  };
}

function buildResourceRef(value: any, error: unknown = undefined) {
  return {
    value: () => value,
    error: () => error,
    reload: jasmine.createSpy('reload'),
    isLoading: () => false,
  } as any;
}

function buildOrderCancelResponse(): OrderCancelResponse {
  return {
    orderId: 'order-1',
    paymentId: 'payment-1',
    orderStatus: { code: '200', label: 'OK' },
    message: 'message',
  };
}

describe('OrderDetailView', () => {
  let component: OrderDetailView;
  let fixture: ComponentFixture<OrderDetailView>;
  let orderApiSpy: jasmine.SpyObj<OrderApi>;
  let catalogApiSpy: jasmine.SpyObj<CatalogApi>;
  let toasterSpy: jasmine.SpyObj<Toaster>;
  let breakpointSubject: Subject<BreakpointState>;

  beforeEach(() => {
    orderApiSpy = jasmine.createSpyObj('OrderApi', ['trackOrderStatus', 'cancelOrder']);
    orderApiSpy.trackOrderStatus.and.returnValue(of({ code: 'CREATED', label: 'Created' }));
    catalogApiSpy = jasmine.createSpyObj('CatalogApi', ['getSlotById']);
    catalogApiSpy.getSlotById.and.returnValue(
      of({ startTime: '2026-08-20T10:00:00', endTime: '2026-08-20T12:00:00' }),
    );
    toasterSpy = jasmine.createSpyObj('Toaster', ['error', 'success']);
    breakpointSubject = new Subject<BreakpointState>();

    TestBed.configureTestingModule({
      imports: [OrderDetailView],
      providers: [
        provideZonelessChangeDetection(),
        { provide: OrderApi, useValue: orderApiSpy },
        { provide: CatalogApi, useValue: catalogApiSpy },
        { provide: Toaster, useValue: toasterSpy },
        {
          provide: BreakpointObserver,
          useValue: { observe: () => breakpointSubject.asObservable() },
        },
      ],
    });

    TestBed.overrideComponent(OrderDetailView, {
      remove: {
        imports: [
          OrderTracking,
          PaymentSummary,
          LocationSummary,
          OrderSummary,
          ReviewOrder,
          InvoiceDownload,
        ],
      },
      add: {
        imports: [
          OrderTrackingStub,
          PaymentSummaryStub,
          LocationSummaryStub,
          OrderSummaryStub,
          ReviewOrderStub,
          InvoiceDownloadStub,
        ],
      },
    });

    fixture = TestBed.createComponent(OrderDetailView);
    component = fixture.componentInstance;
  });

  async function setOrderResult(value: any, error: unknown = undefined) {
    fixture.componentRef.setInput('orderResult', buildResourceRef(value, error));
    await fixture.whenStable();
  }

  it('should create', async () => {
    await setOrderResult(buildOrderResponse());
    expect(component).toBeTruthy();
  });

  describe('orderErrorMessage', () => {
    it('is null when there is no error', async () => {
      await setOrderResult(buildOrderResponse());
      expect(component.orderErrorMessage()).toBeNull();
    });

    it('maps an HttpErrorResponse via mapHttpError', async () => {
      await setOrderResult(buildOrderResponse(), new HttpErrorResponse({ status: 404 }));
      expect(component.orderErrorMessage()).toBe('Not found');
    });
  });

  describe('createdAt', () => {
    it('converts the ISO LocalDateTime string to a JS Date', async () => {
      await setOrderResult(buildOrderResponse({ createdAt: '2026-08-20T10:30:00' }));

      const result = component.createdAt();
      expect(result.getFullYear()).toBe(2026);
      expect(result.getMonth()).toBe(7); // August = index 7
      expect(result.getDate()).toBe(20);
      expect(result.getHours()).toBe(10);
      expect(result.getMinutes()).toBe(30);
    });

    it('falls back to "now" when createdAt is empty', async () => {
      const before = Date.now();
      await setOrderResult(buildOrderResponse({ createdAt: '' }));

      expect(component.createdAt().getTime()).toBeGreaterThanOrEqual(before);
    });
  });

  describe('orderNumber / deliveryAddress', () => {
    it('returns the actual order id when present', async () => {
      await setOrderResult(buildOrderResponse({ orderId: 'order-42' }));
      expect(component.orderNumber()).toBe('order-42');
    });

    it("returns Can't get order number' when order id is empty ", async () => {
      await setOrderResult(buildOrderResponse({ orderId: '' }));

      expect(component.orderNumber()).toBe("Can't get order number");
    });

    it('falls back to a placeholder when deliveryAddress is empty', async () => {
      await setOrderResult(buildOrderResponse({ deliveryAddress: '' }));
      expect(component.deliveryAddress()).toBe('Cannot get the delivery address');
    });

    it('returns the actual delivery address when present', async () => {
      await setOrderResult(buildOrderResponse({ deliveryAddress: '123 Main St' }));
      expect(component.deliveryAddress()).toBe('123 Main St');
    });
  });

  describe('orderArriveIn', () => {
    it('is null when there is no slot yet', async () => {
      await setOrderResult(buildOrderResponse());
      expect(component.orderArriveIn()).toBeNull();
    });

    it('returns "Arriving soon" when the slot end time is in the past', async () => {
      await setOrderResult(buildOrderResponse());
      component.slot.set({
        startTime: LocalDateTime.now().minusHours(2),
        endTime: LocalDateTime.now().minusHours(1),
      });

      expect(component.orderArriveIn()).toBe('Arriving soon');
    });

    it('formats remaining days (pluralized)', async () => {
      await setOrderResult(buildOrderResponse());
      component.slot.set({
        startTime: LocalDateTime.now(),
        endTime: LocalDateTime.now().plusDays(2).plusHours(1),
      });

      expect(component.orderArriveIn()).toBe('Arrive in 2 days');
    });

    it('formats remaining hours (singular) when under a day', async () => {
      await setOrderResult(buildOrderResponse());
      component.slot.set({
        startTime: LocalDateTime.now(),
        endTime: LocalDateTime.now().plusHours(1).plusMinutes(5),
      });

      expect(component.orderArriveIn()).toBe('Arrive in 1 hour');
    });

    it('formats remaining minutes when under an hour', async () => {
      await setOrderResult(buildOrderResponse());
      component.slot.set({
        startTime: LocalDateTime.now(),
        endTime: LocalDateTime.now().plusMinutes(30),
      });

      expect(component.orderArriveIn()).toBe('Arrive in 30 minutes');
    });
  });

  describe('slot effect', () => {
    it('fetches and sets the slot when slotId is present', async () => {
      await setOrderResult(buildOrderResponse({ slotId: 'slot-1' }));

      expect(catalogApiSpy.getSlotById).toHaveBeenCalledWith('slot-1');
      expect(component.slot()).not.toBeNull();
    });

    it('does not fetch when slotId is empty', async () => {
      await setOrderResult(buildOrderResponse({ slotId: '' }));

      expect(catalogApiSpy.getSlotById).not.toHaveBeenCalled();
    });
  });

  describe('cancelOrder', () => {
    it('does nothing when there is no orderId', async () => {
      await setOrderResult(buildOrderResponse({ orderId: '' }));

      component.cancelOrder();

      expect(orderApiSpy.cancelOrder).not.toHaveBeenCalled();
    });

    it('does nothing when already cancelling', async () => {
      await setOrderResult(buildOrderResponse({ orderId: 'order-1' }));
      component.isCancelling.set(true);

      component.cancelOrder();

      expect(orderApiSpy.cancelOrder).not.toHaveBeenCalled();
    });

    it('on success: resets isCancelling, shows a hotToast, and reloads the order', async () => {
      const resourceRef = buildResourceRef(buildOrderResponse({ orderId: 'order-1' }));
      fixture.componentRef.setInput('orderResult', resourceRef);
      await fixture.whenStable();

      orderApiSpy.cancelOrder.and.returnValue(of(buildOrderCancelResponse()));

      component.cancelOrder();

      expect(orderApiSpy.cancelOrder).toHaveBeenCalledWith('order-1');
      expect(component.isCancelling()).toBeFalse();
      expect(toasterSpy.success).toHaveBeenCalledWith('Order cancelled successfully');
      expect(resourceRef.reload).toHaveBeenCalled();
    });

    it('on error: resets isCancelling, shows a toaster error', async () => {
      await setOrderResult(buildOrderResponse({ orderId: 'order-1' }));
      orderApiSpy.cancelOrder.and.returnValue(throwError(() => new Error('failed')));
      spyOn(console, 'error');

      component.cancelOrder();

      expect(component.isCancelling()).toBeFalse();
      expect(toasterSpy.error).toHaveBeenCalledWith('Failed to cancel order. Please try again.');
    });

    it('intermediate state: set isCancelling to true, change button text to Cancelling...', async () => {
      await setOrderResult(buildOrderResponse({ orderId: 'order-1' }));
      const subject = new Subject<OrderCancelResponse>();
      orderApiSpy.cancelOrder.and.returnValue(subject);

      component.cancelOrder();
      expect(component.isCancelling()).toBeTrue();

      fixture.detectChanges();

      const button: HTMLButtonElement = fixture.nativeElement.querySelector(
        'button[matButton="filled"]',
      );
      expect(button.disabled).toBeTrue();
      expect(button.textContent).toContain('Cancelling...');

      subject.next(buildOrderCancelResponse());
      subject.complete();
      await fixture.whenStable();
      expect(component.isCancelling()).toBeFalse();
      expect(toasterSpy.success).toHaveBeenCalledWith('Order cancelled successfully');
    });
  });

  describe('template', () => {
    it('shows the mapped error message when the resource errors', async () => {
      await setOrderResult(buildOrderResponse(), new HttpErrorResponse({ status: 404 }));

      expect(fixture.nativeElement.textContent).toContain('Not found');
    });

    it('renders nothing when there is no error and the order has no orderId yet', async () => {
      await setOrderResult(buildOrderResponse({ orderId: '' }));

      expect(fixture.nativeElement.querySelector('app-order-tracking')).toBeFalsy();
      expect(fixture.nativeElement.querySelector('button.danger')).toBeFalsy();
    });

    it('shows the Cancel Order button, disabled and relabeled, while cancelling', async () => {
      await setOrderResult(buildOrderResponse({ orderId: 'order-1' }));
      component.isCancelling.set(true);
      fixture.detectChanges();
      await fixture.whenStable();

      const button: HTMLButtonElement = fixture.nativeElement.querySelector('button.danger');
      expect(button.disabled).toBeTrue();
      expect(button.textContent).toContain('Cancelling...');
    });

    it('clicking Cancel Order calls cancelOrder()', async () => {
      await setOrderResult(buildOrderResponse({ orderId: 'order-1' }));
      orderApiSpy.cancelOrder.and.returnValue(of(buildOrderCancelResponse()));
      spyOn(component, 'cancelOrder').and.callThrough();

      const button: HTMLButtonElement = fixture.nativeElement.querySelector('button.danger');
      button.click();
      await fixture.whenStable();

      expect(component.cancelOrder).toHaveBeenCalled();
    });

    it('renders the two-column desktop layout when isMobile is false', async () => {
      await setOrderResult(buildOrderResponse({ orderId: 'order-1' }));
      breakpointSubject.next({ matches: false, breakpoints: {} });
      await fixture.whenStable();

      expect(fixture.nativeElement.querySelector('.flex.gap-5')).toBeTruthy();
    });

    it('renders the single-column mobile layout when isMobile is true', async () => {
      await setOrderResult(buildOrderResponse({ orderId: 'order-1' }));
      breakpointSubject.next({ matches: true, breakpoints: {} });
      fixture.detectChanges();
      await fixture.whenStable();

      expect(fixture.nativeElement.querySelector('.flex.gap-5')).toBeFalsy();
    });
  });
});
