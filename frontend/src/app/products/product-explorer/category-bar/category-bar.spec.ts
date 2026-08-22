import { Component, provideZonelessChangeDetection } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { BehaviorSubject, of, Subject } from 'rxjs';
import { BreakpointObserver, BreakpointState } from '@angular/cdk/layout';
import { CategoryBar } from './category-bar';
import { CatalogApi, CategoryResponse } from '@shared/service/catalog-api';
import { CategorySquaredButton } from '../category-bar-squared-button/category-squared-button';

@Component({
  selector: 'app-category-squared-button',
  template: '',
  standalone: true,
  inputs: ['category'],
})
class CategorySquaredButtonStub {
  category: unknown;
}

function buildCategory(overrides: Partial<CategoryResponse> = {}): CategoryResponse {
  return { id: 'cat-1', name: 'Dairy', imageUrl: 'dairy.png', ...overrides } as CategoryResponse;
}

describe('CategoryBar', () => {
  let component: CategoryBar;
  let fixture: ComponentFixture<CategoryBar>;
  let catalogApiSpy: jasmine.SpyObj<CatalogApi>;
  let breakpointSubject: BehaviorSubject<BreakpointState>;

  function createComponent(): void {
    fixture = TestBed.createComponent(CategoryBar);
    component = fixture.componentInstance;
  }

  beforeEach(async () => {
    catalogApiSpy = jasmine.createSpyObj('CatalogApi', ['getCategories']);
    catalogApiSpy.getCategories.and.returnValue(of([]));
    breakpointSubject = new BehaviorSubject<BreakpointState>({ matches: false, breakpoints: {} });

    TestBed.configureTestingModule({
      imports: [CategoryBar],
      providers: [
        provideZonelessChangeDetection(),
        { provide: CatalogApi, useValue: catalogApiSpy },
        {
          provide: BreakpointObserver,
          useValue: { observe: () => breakpointSubject.asObservable() },
        },
      ],
    });

    TestBed.overrideComponent(CategoryBar, {
      remove: { imports: [CategorySquaredButton] },
      add: { imports: [CategorySquaredButtonStub] },
    });
  });

  it('should create', async () => {
    createComponent();
    await fixture.whenStable();
    expect(component).toBeTruthy();
  });

  it('fetches categories with count: 8', async () => {
    createComponent();
    await fixture.whenStable();
    expect(catalogApiSpy.getCategories).toHaveBeenCalledWith(8);
  });

  describe('categoriesFirst / categoriesSecond', () => {
    it('splits into the first 4 and next 4 categories', async () => {
      const categories = Array.from({ length: 8 }, (_, i) =>
        buildCategory({ id: `cat-${i}`, name: `Cat ${i}` }),
      );
      catalogApiSpy.getCategories.and.returnValue(of(categories));

      createComponent();
      await fixture.whenStable();

      expect(component.categoriesFirst().map((c) => c.id)).toEqual([
        'cat-0',
        'cat-1',
        'cat-2',
        'cat-3',
      ]);
      expect(component.categoriesSecond().map((c) => c.id)).toEqual([
        'cat-4',
        'cat-5',
        'cat-6',
        'cat-7',
      ]);
    });

    it('does not throw when fewer than 8 categories are returned', async () => {
      catalogApiSpy.getCategories.and.returnValue(of([buildCategory({ id: 'cat-0' })]));
      createComponent();
      await fixture.whenStable();

      expect(component.categoriesFirst().length).toBe(1);
      expect(component.categoriesSecond().length).toBe(0);
    });

    it('returns empty arrays before the resource resolves', async () => {
      catalogApiSpy.getCategories.and.returnValue(of([]));

      createComponent();
      await fixture.whenStable();

      expect(component.categoriesFirst()).toEqual([]);
      expect(component.categoriesSecond()).toEqual([]);
    });
  });

  describe('isSquaredButton', () => {
    it('reflects BreakpointObserver matches for max-width: 1040px', async () => {
      createComponent();
      await fixture.whenStable();

      expect(component.isSquaredButton()).toBeFalse();

      breakpointSubject.next({ matches: true, breakpoints: {} });
      await fixture.whenStable();

      expect(component.isSquaredButton()).toBeTrue();
    });
  });
});
