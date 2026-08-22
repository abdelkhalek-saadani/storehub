import { Component, ElementRef, provideZonelessChangeDetection } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { BehaviorSubject, of, throwError } from 'rxjs';
import { BreakpointObserver, BreakpointState } from '@angular/cdk/layout';
import { BestSellerSection } from './best-seller-section';
import { CatalogApi } from '@shared/service/catalog-api';
import { ScrollArrows } from '../scroll-arrows/scroll-arrows';
import { ProductCard } from '../../product-card/product-card';
import { provideRouter } from '@angular/router';

@Component({ selector: 'app-scroll-arrows', template: '', standalone: true })
class ScrollArrowsStub {}

@Component({ selector: 'app-product-card', template: '', standalone: true, inputs: ['product'] })
class ProductCardStub {
  product: unknown;
}

describe('BestSellerSection', () => {
  let component: BestSellerSection;
  let fixture: ComponentFixture<BestSellerSection>;
  let catalogApiSpy: jasmine.SpyObj<CatalogApi>;
  let breakpointSubject: BehaviorSubject<BreakpointState>;

  function createComponent(): void {
    fixture = TestBed.createComponent(BestSellerSection);
    component = fixture.componentInstance;
  }

  beforeEach(async () => {
    catalogApiSpy = jasmine.createSpyObj('CatalogApi', ['getBestSellerProducts']);
    catalogApiSpy.getBestSellerProducts.and.returnValue(of([]));
    breakpointSubject = new BehaviorSubject<BreakpointState>({ matches: false, breakpoints: {} });

    TestBed.configureTestingModule({
      imports: [BestSellerSection],
      providers: [
        provideZonelessChangeDetection(),
        provideRouter([]),
        { provide: CatalogApi, useValue: catalogApiSpy },
        {
          provide: BreakpointObserver,
          useValue: { observe: () => breakpointSubject.asObservable() },
        },
      ],
    });

    TestBed.overrideComponent(BestSellerSection, {
      remove: { imports: [ScrollArrows, ProductCard] },
      add: { imports: [ScrollArrowsStub, ProductCardStub] },
    });

    createComponent();
    await fixture.whenStable();
  });

  it('should create', async () => {
    expect(component).toBeTruthy();
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
    it('calls products.reload()', async () => {
      spyOn(component.products, 'reload');

      component.retry();

      expect(component.products.reload).toHaveBeenCalled();
    });
  });

  describe('template', () => {
    it('shows the error state and Retry button when the resource errors', async () => {
      catalogApiSpy.getBestSellerProducts.and.returnValue(throwError(() => new Error('failed')));
      createComponent(); // called again here so products rxResource retries to fetch
      await fixture.whenStable();

      expect(fixture.nativeElement.textContent).toContain('Failed to load products');
      expect(fixture.nativeElement.textContent).toContain('Retry');
    });

    it('renders one product card per best seller on success', async () => {
      catalogApiSpy.getBestSellerProducts.and.returnValue(
        of([{ id: 'p1' } as any, { id: 'p2' }, { id: 'p3' }]),
      );
      createComponent();
      await fixture.whenStable();

      const cards = fixture.nativeElement.querySelectorAll('app-product-card');
      expect(cards.length).toBe(3);
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
