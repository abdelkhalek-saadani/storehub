import { TestBed } from '@angular/core/testing';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { StoreApi } from './store-api';
import { environment } from '@environments/environment';
import { Store } from '@shared/models/Store';
import { provideHttpClient } from '@angular/common/http';
import { provideZonelessChangeDetection } from '@angular/core';

describe('StoreApi', () => {
  let service: StoreApi;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [],
      providers: [
        StoreApi,
        provideZonelessChangeDetection(),
        provideHttpClient(),
        provideHttpClientTesting(),
      ],
    });
    service = TestBed.inject(StoreApi);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('getAllStores GETs the stores list', () => {
    const mockStores: Store[] = [{ storeId: '1', storeSlug: 'a', storeName: 'A' }];

    service.getAllStores().subscribe((res) => {
      expect(res).toEqual(mockStores);
    });

    const req = httpMock.expectOne(`${environment.orderApiUrl}/api/stores`);
    expect(req.request.method).toBe('GET');
    req.flush(mockStores);
  });

  it('createStore POSTs the payload', () => {
    const payload = { name: 'Super Mart', description: 'desc', address: 'addr' };

    service.createStore(payload).subscribe();

    const req = httpMock.expectOne(`${environment.orderApiUrl}/api/stores`);
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual(payload);
    req.flush({});
  });

  it('getStoreBySlug GETs the store by slug', () => {
    const mockStore: Store = {
      storeId: '1',
      storeSlug: 'super-mart',
      storeName: 'Super Mart',
    };

    service.getStoreBySlug('super-mart').subscribe((res) => {
      expect(res).toEqual(mockStore);
    });

    const req = httpMock.expectOne(`${environment.orderApiUrl}/api/stores/by-slug/super-mart`);
    expect(req.request.method).toBe('GET');
    req.flush(mockStore);
  });
});
