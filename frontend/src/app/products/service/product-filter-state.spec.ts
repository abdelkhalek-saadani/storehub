import { TestBed } from '@angular/core/testing';
import { BehaviorSubject } from 'rxjs';
import { ActivatedRoute, Router, convertToParamMap, ParamMap } from '@angular/router';
import { ProductFilterState } from './product-filter-state';
import { provideZonelessChangeDetection } from '@angular/core';

describe('ProductFilterState', () => {
  let service: ProductFilterState;
  let routerSpy: jasmine.SpyObj<Router>;

  function setup(initialParams: Record<string, string> = {}) {
    const queryParamMapSubject = new BehaviorSubject<ParamMap>(convertToParamMap(initialParams));
    routerSpy = jasmine.createSpyObj('Router', ['navigate']);

    TestBed.configureTestingModule({
      providers: [
        provideZonelessChangeDetection(),
        ProductFilterState,
        { provide: Router, useValue: routerSpy },
        {
          provide: ActivatedRoute,
          useValue: { queryParamMap: queryParamMapSubject.asObservable(), snapshot: {} },
        },
      ],
    });

    service = TestBed.inject(ProductFilterState);
  }

  describe('filters()', () => {
    it('returns empty categories and null price/saleEvent/isBestSeller when params are absent', () => {
      setup({});
      expect(service.filters()).toEqual({
        categories: [],
        minPrice: null,
        maxPrice: null,
        saleEvent: null,
        isBestSeller: null,
      });
    });

    it('splits categories by comma', () => {
      setup({ categories: 'Dairy,Fresh' });
      expect(service.filters().categories).toEqual(['Dairy', 'Fresh']);
    });

    it('parses minPrice and maxPrice as numbers', () => {
      setup({ minPrice: '10', maxPrice: '50' });
      expect(service.filters().minPrice).toBe(10);
      expect(service.filters().maxPrice).toBe(50);
    });

    it('parses isBestSeller "true" (case-insensitive) as true', () => {
      setup({ isBestSeller: 'TRUE' });
      expect(service.filters().isBestSeller).toBeTrue();
    });

    it('parses isBestSeller as false when present but not "true"', () => {
      setup({ isBestSeller: 'false' });
      expect(service.filters().isBestSeller).toBeFalse();
    });

    it('leaves isBestSeller null when the param is absent', () => {
      setup({});
      expect(service.filters().isBestSeller).toBeNull();
    });
  });

  describe('setCategories', () => {
    it('joins categories with a comma and resets page to 0', () => {
      setup({});
      service.setCategories(['Dairy', 'Fresh']);

      expect(routerSpy.navigate).toHaveBeenCalledWith(
        [],
        jasmine.objectContaining({
          queryParams: { categories: 'Dairy,Fresh', page: '0' },
          queryParamsHandling: 'merge',
          replaceUrl: true,
        }),
      );
    });

    it('sends null for categories when the array is empty', () => {
      setup({});
      service.setCategories([]);

      expect(routerSpy.navigate).toHaveBeenCalledWith(
        [],
        jasmine.objectContaining({ queryParams: { categories: null, page: '0' } }),
      );
    });
  });

  describe('removeCategory', () => {
    it('removes only the given category and re-applies the rest', () => {
      setup({ categories: 'Dairy,Fresh,Organic' });

      service.removeCategory('Fresh');

      expect(routerSpy.navigate).toHaveBeenCalledWith(
        [],
        jasmine.objectContaining({
          queryParams: { categories: 'Dairy,Organic', page: '0' },
        }),
      );
    });
  });

  describe('setPrice', () => {
    it('stringifies min/max price', () => {
      setup({});
      service.setPrice(10, 50);

      expect(routerSpy.navigate).toHaveBeenCalledWith(
        [],
        jasmine.objectContaining({
          queryParams: { minPrice: '10', maxPrice: '50', page: '0' },
        }),
      );
    });

    it('documents a bug: a price of 0 is falsy and gets cleared instead of set to "0"', () => {
      setup({});
      service.setPrice(0, null);

      expect(routerSpy.navigate).toHaveBeenCalledWith(
        [],
        jasmine.objectContaining({
          queryParams: { minPrice: null, maxPrice: null, page: '0' },
        }),
      );
    });
  });

  describe('clearMinPrice / clearMaxPrice', () => {
    it('clearMinPrice patches only minPrice', () => {
      setup({});
      service.clearMinPrice();

      expect(routerSpy.navigate).toHaveBeenCalledWith(
        [],
        jasmine.objectContaining({ queryParams: { minPrice: null, page: '0' } }),
      );
    });

    it('clearMaxPrice patches only maxPrice', () => {
      setup({});
      service.clearMaxPrice();

      expect(routerSpy.navigate).toHaveBeenCalledWith(
        [],
        jasmine.objectContaining({ queryParams: { maxPrice: null, page: '0' } }),
      );
    });
  });
});
