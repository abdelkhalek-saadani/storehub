import { TestBed } from '@angular/core/testing';
import { Router, UrlTree } from '@angular/router';
import { storeRequiredGuard } from './store-required.guard';
import { StoreContext } from '../service/store-context';
import { provideZonelessChangeDetection } from '@angular/core';

describe('storeRequiredGuard', () => {
  let routerSpy: jasmine.SpyObj<Router>;
  let storeContextMock: { getCurrentStore: jasmine.Spy };

  beforeEach(() => {
    routerSpy = jasmine.createSpyObj('Router', ['parseUrl']);
    routerSpy.parseUrl.and.callFake((url: string) => ({ __urlTree: url }) as unknown as UrlTree);
    storeContextMock = { getCurrentStore: jasmine.createSpy('getCurrentStore') };

    TestBed.configureTestingModule({
      providers: [
        provideZonelessChangeDetection(),
        { provide: Router, useValue: routerSpy },
        { provide: StoreContext, useValue: storeContextMock },
      ],
    });
  });

  it('redirects to the cached store slug when a store is cached', () => {
    storeContextMock.getCurrentStore.and.returnValue({ slug: 'super-mart' });

    TestBed.runInInjectionContext(() => storeRequiredGuard({} as any, { url: '/products' } as any));

    expect(routerSpy.parseUrl).toHaveBeenCalledWith('/store/super-mart/products');
  });

  it('redirects to the store picker with an encoded returnUrl when no store is cached', () => {
    storeContextMock.getCurrentStore.and.returnValue(null);

    TestBed.runInInjectionContext(() =>
      storeRequiredGuard({} as any, { url: '/products?foo=bar' } as any),
    );

    expect(routerSpy.parseUrl).toHaveBeenCalledWith(
      `/welcome-pick-store?returnUrl=${encodeURIComponent('/products?foo=bar')}`,
    );
  });

  it('redirects to the store picker when the cached store has no slug', () => {
    storeContextMock.getCurrentStore.and.returnValue({ slug: null });

    TestBed.runInInjectionContext(() => storeRequiredGuard({} as any, { url: '/products' } as any));

    expect(routerSpy.parseUrl).toHaveBeenCalledWith(
      `/welcome-pick-store?returnUrl=${encodeURIComponent('/products')}`,
    );
  });
});
