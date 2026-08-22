import {
  Component,
  computed,
  ElementRef,
  inject,
  OnInit,
  resource,
  signal,
  ViewChild,
} from '@angular/core';
import { MatButton, MatIconButton } from '@angular/material/button';
import { MatIcon } from '@angular/material/icon';
import { BreakpointObserver } from '@angular/cdk/layout';
import { rxResource, takeUntilDestroyed, toSignal } from '@angular/core/rxjs-interop';
import { NgClass } from '@angular/common';
import { ScrollArrows } from '../scroll-arrows/scroll-arrows';
import { ProductCard } from '../../product-card/product-card';
import { CatalogApi } from '@shared/service/catalog-api';
import { catchError, of } from 'rxjs';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-best-seller-section',
  imports: [MatButton, MatIcon, NgClass, ScrollArrows, ProductCard, RouterLink],
  template: `
    <div
      class="flex flex-col gap-4 md:gap-6 md:p-6 md:rounded-lg md:border md:border-[#F0EEF0] md:bg-white"
    >
      <div class="flex items-center justify-between">
        <span class="font-semibold text-black text-lg md:text-2xl ">Best Seller</span>
        <div class="flex items-center gap-6">
          <button
            matButton="filled"
            routerLink="../products"
            [queryParams]="{ isBestSeller: true }"
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
      @if (products.error()) {
        <div class="flex flex-col items-center gap-3 py-12">
          <span class="text-gray-500">Failed to load products. Please try again.</span>
          <button matButton="filled" (click)="retry()">Retry</button>
        </div>
      } @else {
        <div
          #scrollContainer
          class="flex gap-4 pb-2 overflow-x-auto snap-x snap-mandatory scroll-smooth
           [-ms-overflow-style:none] [scrollbar-width:none] [&::-webkit-scrollbar]:hidden"
        >
          @for (product of products.value(); track product.id) {
            <app-product-card [product]="product" class="snap-start " />
          }
        </div>
      }
    </div>
  `,
  styles: ``,
})
export class BestSellerSection {
  private breakpointObserver = inject(BreakpointObserver);
  private catalogApi = inject(CatalogApi);
  isMobile = signal(false);
  @ViewChild('scrollContainer') scrollContainer!: ElementRef;

  products = rxResource({
    stream: () => this.catalogApi.getBestSellerProducts(),
  });

  retry() {
    this.products.reload();
  }

  constructor() {
    this.breakpointObserver
      .observe('(max-width: 768px)')
      .pipe(takeUntilDestroyed())
      .subscribe((result) => this.isMobile.set(result.matches));
  }

  scroll(direction: 'left' | 'right') {
    this.scrollContainer.nativeElement.scrollBy({
      left: direction === 'right' ? 300 : -300,
      behavior: 'smooth',
    });
  }
}
