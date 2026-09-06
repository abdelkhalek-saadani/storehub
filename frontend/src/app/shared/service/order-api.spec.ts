import { TestBed } from '@angular/core/testing';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { OrderApi, OrderRequest } from './order-api';
import { StoreContext } from '../../store/service/store-context';
import { environment } from '@environments/environment';
import { provideHttpClient } from '@angular/common/http';
import { provideZonelessChangeDetection } from '@angular/core';
import { ConfigService } from '@core/config.service';

describe('OrderApi', () => {
  let api: OrderApi;
  let httpMock: HttpTestingController;
  let storeContextSpy: jasmine.SpyObj<StoreContext>;
  let configServiceSpy: jasmine.SpyObj<ConfigService>;

  beforeEach(() => {
    storeContextSpy = jasmine.createSpyObj('StoreContext', ['storeId']);
    storeContextSpy.storeId.and.returnValue('store-1');

    configServiceSpy = jasmine.createSpyObj('ConfigService', ['get']);
    configServiceSpy.get.and.returnValue({
      orderApiUrl: 'http://localhost:8080',
      catalogApiUrl: 'http://localhost:8080',
      kcUrl: 'http://localhost:8088',
    });

    TestBed.configureTestingModule({
      providers: [
        provideZonelessChangeDetection(),
        provideHttpClient(),
        provideHttpClientTesting(),
        OrderApi,
        { provide: StoreContext, useValue: storeContextSpy },
        { provide: ConfigService, useValue: configServiceSpy },
      ],
    });

    api = TestBed.inject(OrderApi);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => httpMock.verify());

  it('throws if storeId is not available', () => {
    storeContextSpy.storeId.and.returnValue(null);
    expect(() => api.getCart()).toThrowError('OrderApi called before storeId is available');
  });

  it('getCart sends GET with storeId param', () => {
    api.getCart().subscribe();

    const req = httpMock.expectOne(
      (r) => r.url === `http://localhost:8080/api/cart` && r.params.get('storeId') === 'store-1',
    );
    expect(req.request.method).toBe('GET');
    req.flush({});
  });

  it('placeOrder sends POST with idempotency header and storeId merged into body', () => {
    const request: OrderRequest = { slotId: 's1', cartId: 'c1' };
    api.placeOrder('idem-123', request).subscribe();

    const req = httpMock.expectOne(`http://localhost:8080/api/orders`);
    expect(req.request.method).toBe('POST');
    expect(req.request.headers.get('Idempotency-Key')).toBe('idem-123');
    expect(req.request.body).toEqual({ ...request, storeId: 'store-1' });
    req.flush({});
  });

  it('clearCart sends DELETE with storeId param', () => {
    api.clearCart().subscribe();

    const req = httpMock.expectOne(
      (r) => r.url === `http://localhost:8080/api/cart` && r.params.get('storeId') === 'store-1',
    );
    expect(req.request.method).toBe('DELETE');
    req.flush({});
  });

  it('upsertItems sends POST with storeId and items in body', () => {
    const items = [{ productId: 'p1', quantity: 2 } as any];
    api.upsertItems(items).subscribe();

    const req = httpMock.expectOne(`http://localhost:8080/api/cart/items`);
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual({ storeId: 'store-1', items });
    req.flush({});
  });
});
