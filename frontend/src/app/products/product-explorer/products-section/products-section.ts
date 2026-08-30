import { Component, computed, ElementRef, inject, Signal, signal, ViewChild } from '@angular/core';
import { MatButton } from '@angular/material/button';
import { MatIcon } from '@angular/material/icon';
import { BreakpointObserver } from '@angular/cdk/layout';
import { rxResource, takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { NgClass } from '@angular/common';
import { ScrollArrows } from '../scroll-arrows/scroll-arrows';
import { ProductCard } from '../../product-card/product-card';
import { PagedResponse } from '../../models/page-response';
import { Product } from '../../models/product';
import { ProductQuery, CatalogApi } from '@shared/service/catalog-api';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-products-section',
  imports: [ProductCard, MatButton, MatIcon, NgClass, ScrollArrows, RouterLink],
  template: `
    <div
      class="flex flex-col gap-4 pt-6 md:gap-6 md:p-6 md:rounded-lg md:border md:border-[#F0EEF0] md:bg-white md:mt-8"
    >
      <div class="flex items-center justify-between">
        <span class="font-semibold text-black text-lg md:text-2xl ">Products</span>
        <div class="flex items-center gap-6">
          <button
            matButton="filled"
            routerLink="../products"
            [ngClass]="isMobile() ? 'btn-sm' : 'btn-md'"
          >
            View All
            <mat-icon iconPositionEnd>arrow_forward</mat-icon>
          </button>
          @if (!isMobile()) {
            <app-scroll-arrows (arrowClicked)="scroll($event)" />
          }
        </div>
      </div>
      @if (pagedResult.error()) {
        <div class="flex flex-col items-center gap-3 py-12">
          <span class="text-gray-500">Failed to load products. Please try again.</span>
          <button matButton="filled" (click)="retry()">Retry</button>
        </div>
      } @else {
        <div
          #scrollContainer
          class="grid grid-rows-2 grid-flow-col gap-4 overflow-x-auto snap-x snap-mandatory scroll-smooth px-4 pb-4 [scrollbar-width:none] [-ms-overflow-style:none] [&::-webkit-scrollbar]:hidden"
        >
          @for (product of pagedResult.value().content; track product.id) {
            <app-product-card [product]="product" data-cy="product-card" />
          }
        </div>
      }
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

  pagedResult = rxResource<PagedResponse<Product>, ProductQuery>({
    params: () => ({ page: 0, size: 20 }),
    stream: ({ params }) => {
      return this.productService.getProducts(params);
    },
    defaultValue: {
      content: [],
      totalElements: 0,
      totalPages: 0,
      number: 0,
      size: 0,
    },
  });

  retry() {
    this.pagedResult.reload();
  }

  private productService = inject(CatalogApi);

  constructor() {
    this.breakpointObserver
      .observe('(max-width: 768px)')
      .pipe(takeUntilDestroyed())
      .subscribe((result) => this.isMobile.set(result.matches));
  }
}
