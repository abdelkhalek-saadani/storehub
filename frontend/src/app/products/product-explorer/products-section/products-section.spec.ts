import { Component, ElementRef, provideZonelessChangeDetection } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { BehaviorSubject, of, throwError } from 'rxjs';
import { BreakpointObserver, BreakpointState } from '@angular/cdk/layout';
import { ProductsSection } from './products-section';
import { CatalogApi } from '@shared/service/catalog-api';
import { ScrollArrows } from '../scroll-arrows/scroll-arrows';
import { ProductCard } from '../../product-card/product-card';
import { Product } from '../../models/product';
import { provideRouter } from '@angular/router';

@Component({ selector: 'app-scroll-arrows', template: '', standalone: true })
class ScrollArrowsStub {}

@Component({ selector: 'app-product-card', template: '', standalone: true, inputs: ['product'] })
class ProductCardStub {
  product: unknown;
}

function buildProduct(overrides?: Partial<Product>): Product {
  return {
    id: 'p1',
    storeId: 's1',
    name: 'pname1',
    description: 'description',
    unitPrice: 10,
    finalPrice: 10,
    activeDiscount: null,
    ...overrides,
  };
}

describe('ProductsSection', () => {
  let component: ProductsSection;
  let fixture: ComponentFixture<ProductsSection>;
  let catalogApiSpy: jasmine.SpyObj<CatalogApi>;
  let breakpointSubject: BehaviorSubject<BreakpointState>;

  function createComponent() {
    fixture = TestBed.createComponent(ProductsSection);
    component = fixture.componentInstance;
  }

  beforeEach(async () => {
    catalogApiSpy = jasmine.createSpyObj('CatalogApi', ['getProducts']);
    catalogApiSpy.getProducts.and.returnValue(
      of({ content: [], totalElements: 0, totalPages: 0, number: 0, size: 20 }),
    );
    breakpointSubject = new BehaviorSubject<BreakpointState>({ matches: false, breakpoints: {} });

    TestBed.configureTestingModule({
      imports: [ProductsSection],
      providers: [
        provideRouter([]),
        provideZonelessChangeDetection(),
        { provide: CatalogApi, useValue: catalogApiSpy },
        {
          provide: BreakpointObserver,
          useValue: { observe: () => breakpointSubject.asObservable() },
        },
      ],
    });

    TestBed.overrideComponent(ProductsSection, {
      remove: { imports: [ScrollArrows, ProductCard] },
      add: { imports: [ScrollArrowsStub, ProductCardStub] },
    });

    fixture = TestBed.createComponent(ProductsSection);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', async () => {
    expect(component).toBeTruthy();
  });

  it('fetches products with the fixed page: 0, size: 20 params', async () => {
    expect(catalogApiSpy.getProducts).toHaveBeenCalledWith(
      jasmine.objectContaining({ page: 0, size: 20 }),
    );
  });

  describe('scroll', () => {
    it('scrolls right by 300px', async () => {
      const scrollBySpy = jasmine.createSpy('scrollBy');
      component.scrollContainer = {
        nativeElement: { scrollBy: scrollBySpy },
      } as unknown as ElementRef;

      component.scroll('right');

      expect(scrollBySpy).toHaveBeenCalledWith({ left: 300, behavior: 'smooth' });
    });

    it('scrolls left by -300px', async () => {
      const scrollBySpy = jasmine.createSpy('scrollBy');
      component.scrollContainer = {
        nativeElement: { scrollBy: scrollBySpy },
      } as unknown as ElementRef;

      component.scroll('left');

      expect(scrollBySpy).toHaveBeenCalledWith({ left: -300, behavior: 'smooth' });
    });
  });

  describe('retry', () => {
    it('calls pagedResult.reload()', async () => {
      spyOn(component.pagedResult, 'reload');

      component.retry();

      expect(component.pagedResult.reload).toHaveBeenCalled();
    });
  });

  describe('template', async () => {
    it('shows the error state and Retry button when the resource errors', async () => {
      catalogApiSpy.getProducts.and.returnValue(throwError(() => new Error('failed')));
      createComponent();
      await fixture.whenStable();

      expect(fixture.nativeElement.textContent).toContain('Failed to load products');
      expect(fixture.nativeElement.textContent).toContain('Retry');
    });

    it('renders one product card per item on success', async () => {
      catalogApiSpy.getProducts.and.returnValue(
        of({
          content: [buildProduct(), buildProduct({ id: 'p2' })],
          totalElements: 2,
          totalPages: 1,
          number: 0,
          size: 20,
        }),
      );
      createComponent();
      await fixture.whenStable();

      const cards = fixture.nativeElement.querySelectorAll('app-product-card');
      expect(cards.length).toBe(2);
    });

    it('hides scroll arrows on mobile', async () => {
      breakpointSubject.next({ matches: true, breakpoints: {} });

      await fixture.whenStable();

      expect(fixture.nativeElement.querySelector('app-scroll-arrows')).toBeFalsy();
    });

    it('shows scroll arrows on desktop', async () => {
      breakpointSubject.next({ matches: false, breakpoints: {} });

      await fixture.whenStable();

      expect(fixture.nativeElement.querySelector('app-scroll-arrows')).toBeTruthy();
    });
  });
});
