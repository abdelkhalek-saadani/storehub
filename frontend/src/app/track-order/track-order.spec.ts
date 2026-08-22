import { Component, provideZonelessChangeDetection } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { of, Subject } from 'rxjs';
import { ActivatedRoute, convertToParamMap } from '@angular/router';
import TrackOrderPage from './track-order';
import { OrderApi } from '@shared/service/order-api';
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

function buildOrderResponse(overrides: Partial<any> = {}) {
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

describe('TrackOrderPage', () => {
  let component: TrackOrderPage;
  let fixture: ComponentFixture<TrackOrderPage>;
  let orderApiSpy: jasmine.SpyObj<OrderApi>;
  let queryParamMapSubject: Subject<any>;

  beforeEach(async () => {
    orderApiSpy = jasmine.createSpyObj('OrderApi', ['getOrder']);
    orderApiSpy.getOrder.and.returnValue(of(buildOrderResponse()));
    queryParamMapSubject = new Subject();

    TestBed.configureTestingModule({
      imports: [TrackOrderPage],
      providers: [
        provideZonelessChangeDetection(),
        { provide: OrderApi, useValue: orderApiSpy },
        {
          provide: ActivatedRoute,
          useValue: { queryParamMap: queryParamMapSubject.asObservable() },
        },
      ],
    });

    TestBed.overrideComponent(TrackOrderPage, {
      remove: { imports: [OrderDetailView] },
      add: { imports: [OrderDetailViewStub] },
    });

    fixture = TestBed.createComponent(TrackOrderPage);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', async () => {
    expect(component).toBeTruthy();
  });

  it('sets token from the "token" query param and triggers a fetch', async () => {
    queryParamMapSubject.next(convertToParamMap({ token: 'tok-123' }));
    await fixture.whenStable();

    expect(component.token()).toBe('tok-123');
    expect(orderApiSpy.getOrder).toHaveBeenCalledWith('tok-123');
  });

  it('keeps token null and does not fetch when the param is absent', async () => {
    queryParamMapSubject.next(convertToParamMap({}));
    await fixture.whenStable();

    expect(component.token()).toBeNull();
    expect(orderApiSpy.getOrder).not.toHaveBeenCalled();
  });
});
