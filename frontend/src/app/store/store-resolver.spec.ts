import { TestBed } from '@angular/core/testing';
import { of, throwError } from 'rxjs';
import { Router } from '@angular/router';
import { StoreContext } from './service/store-context';
import { StoreApi } from './service/store-api';
import { StorePickerService } from './service/store-picker';
import { Store } from '@shared/models/Store';
import { storeResolver } from './store-resolver';
import { provideZonelessChangeDetection } from '@angular/core';

function buildStore(overrides: Partial<Store> = {}): Store {
  return {
    storeId: 'store-1',
    storeSlug: 'super-mart',
    storeName: 'Super Mart',
    ...overrides,
  } as Store;
}

describe('storeResolver', () => {
  let storeContextMock: {
    storeSlug: jasmine.Spy;
    storeId: jasmine.Spy;
    setStore: jasmine.Spy;
  };
  let storeApiSpy: jasmine.SpyObj<StoreApi>;
  let pickerSpy: jasmine.SpyObj<StorePickerService>;
  let routerSpy: jasmine.SpyObj<Router>;

  beforeEach(() => {
    storeContextMock = {
      storeSlug: jasmine.createSpy('storeSlug'),
      storeId: jasmine.createSpy('storeId'),
      setStore: jasmine.createSpy('setStore'),
    };
    storeApiSpy = jasmine.createSpyObj('StoreApi', ['getStoreBySlug']);
    pickerSpy = jasmine.createSpyObj('StorePickerService', ['pickStore']);
    routerSpy = jasmine.createSpyObj('Router', ['navigate']);

    TestBed.configureTestingModule({
      providers: [
        provideZonelessChangeDetection(),
        { provide: StoreContext, useValue: storeContextMock },
        { provide: StoreApi, useValue: storeApiSpy },
        { provide: StorePickerService, useValue: pickerSpy },
        { provide: Router, useValue: routerSpy },
      ],
    });
  });

  function runResolver(paramMapGet: string, url: string) {
    return TestBed.runInInjectionContext(() =>
      storeResolver({ paramMap: { get: () => paramMapGet } } as any, { url } as any),
    );
  }

  it('returns the cached storeId synchronously without an API call when slug matches and storeId exists', () => {
    storeContextMock.storeSlug.and.returnValue('super-mart');
    storeContextMock.storeId.and.returnValue('store-1');

    const result: any = runResolver('super-mart', '/store/super-mart/products');

    expect(result).toBe('store-1');
    expect(storeApiSpy.getStoreBySlug).not.toHaveBeenCalled();
  });

  it('fetches the store by slug and caches it when the slug changed', (done) => {
    storeContextMock.storeSlug.and.returnValue('other-store');
    storeContextMock.storeId.and.returnValue('old-id');
    storeApiSpy.getStoreBySlug.and.returnValue(of(buildStore({ storeId: 'store-2' })));

    const result$: any = runResolver('super-mart', '/store/super-mart/products');

    result$.subscribe((storeId: string) => {
      expect(storeApiSpy.getStoreBySlug).toHaveBeenCalledWith('super-mart');
      expect(storeContextMock.setStore).toHaveBeenCalledWith(
        jasmine.objectContaining({ storeId: 'store-2' }),
      );
      expect(storeId).toBe('store-2');
      done();
    });
  });

  it('fetches even when the slug matches but storeId is missing from cache', (done) => {
    storeContextMock.storeSlug.and.returnValue('super-mart');
    storeContextMock.storeId.and.returnValue(null);
    storeApiSpy.getStoreBySlug.and.returnValue(of(buildStore({ storeId: 'store-1' })));

    const result$: any = runResolver('super-mart', '/store/super-mart/products');

    result$.subscribe((storeId: string) => {
      expect(storeApiSpy.getStoreBySlug).toHaveBeenCalledWith('super-mart');
      expect(storeContextMock.setStore).toHaveBeenCalledWith(
        jasmine.objectContaining({ storeId: 'store-1' }),
      );
      expect(storeId).toBe('store-1');
      done();
    });
  });

  it('falls back to the store picker when the fetch fails, caches the picked store, and re-navigates', (done) => {
    storeContextMock.storeSlug.and.returnValue('other');
    storeContextMock.storeId.and.returnValue(null);
    storeApiSpy.getStoreBySlug.and.returnValue(throwError(() => new Error('not found')));
    const pickedStore = buildStore({ storeId: 'store-9', storeSlug: 'picked-store' });
    pickerSpy.pickStore.and.returnValue(of(pickedStore));

    const result$: any = runResolver('missing-slug', '/store/missing-slug/products');

    result$.subscribe((storeId: string) => {
      expect(pickerSpy.pickStore).toHaveBeenCalled();
      expect(storeContextMock.setStore).toHaveBeenCalledWith(pickedStore);
      expect(routerSpy.navigate).toHaveBeenCalledWith(['/store/picked-store/products']);
      expect(storeId).toBe('store-9');
      done();
    });
  });
});
