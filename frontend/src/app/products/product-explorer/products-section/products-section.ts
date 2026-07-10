import { Component, computed, ElementRef, inject, Signal, signal, ViewChild } from '@angular/core';
import { MatButton } from '@angular/material/button';
import { MatIcon } from '@angular/material/icon';
import { BreakpointObserver } from '@angular/cdk/layout';
import { takeUntilDestroyed, toObservable, toSignal } from '@angular/core/rxjs-interop';
import { NgClass } from '@angular/common';
import { ScrollArrows } from '../scroll-arrows/scroll-arrows';
import { ProductCard } from '../../product-card/product-card';
import { PagedResponse } from '../../models/page-response';
import { Product } from '../../models/product';
import { ProductQuery, ProductService } from '../../service/catalog-api';
import { of, switchMap } from 'rxjs';
import { StoreContext } from '../../service/store-context';

@Component({
  selector: 'app-products-section',
  imports: [ProductCard, MatButton, MatIcon, NgClass, ScrollArrows],
  template: `
    <div
      class="flex flex-col gap-4 pt-6 md:gap-6 md:p-6 md:rounded-lg md:border md:border-[#F0EEF0] md:bg-white md:mt-8"
    >
      <div class="flex items-center justify-between">
        <span class="font-semibold text-black text-lg md:text-2xl ">Products</span>
        <div class="flex items-center gap-6">
          <button matButton="filled" [ngClass]="isMobile() ? 'btn-sm' : 'btn-md'">
            View All
            <mat-icon iconPositionEnd>arrow_forward</mat-icon>
          </button>
          @if (!isMobile()) {
            <app-scroll-arrows (arrowClicked)="scroll($event)" />
          }
        </div>
      </div>
      <div
        #scrollContainer
        class="grid grid-rows-2 grid-flow-col gap-4 overflow-x-auto snap-x snap-mandatory scroll-smooth px-4 pb-4 [scrollbar-width:none] [-ms-overflow-style:none] [&::-webkit-scrollbar]:hidden"
      >
        @for (product of pagedResult()?.content ?? []; track product.id) {
          <app-product-card [product]="product" />
        }
      </div>
    </div>
  `,
  styles: ``,
})
export class ProductsSection {
  private breakpointObserver = inject(BreakpointObserver);
  isMobile = signal(false);
  @ViewChild('scrollContainer') scrollContainer!: ElementRef;

  scroll(direction: 'left' | 'right') {
    this.scrollContainer.nativeElement.scrollBy({
      left: direction === 'right' ? 300 : -300,
      behavior: 'smooth',
    });
  }
  private query = computed(() => {
    const storeId = this.storeContext.storeId();
    return storeId
      ? {
          storeId,
          page: 0,
          size: 10,
          minPrice: 0,
          maxPrice: 99999,
        }
      : null;
  });

  pagedResult: Signal<PagedResponse<Product> | null> = toSignal(
    toObservable(this.query).pipe(
      switchMap((q: ProductQuery | null) => (q ? this.productService.getProducts(q) : of(null))),
    ),
    { initialValue: null },
  );

  private productService = inject(ProductService);
  private storeContext = inject(StoreContext);

  constructor() {
    this.breakpointObserver
      .observe('(max-width: 768px)')
      .pipe(takeUntilDestroyed())
      .subscribe((result) => this.isMobile.set(result.matches));
  }
}
