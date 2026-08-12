import { Component, computed, inject, Signal, signal } from '@angular/core';
import { rxResource, takeUntilDestroyed, toObservable, toSignal } from '@angular/core/rxjs-interop';
import { BreakpointObserver } from '@angular/cdk/layout';
import { NgClass } from '@angular/common';
import { MatPaginator, PageEvent } from '@angular/material/paginator';
import { MatButton, MatIconButton } from '@angular/material/button';
import { MatIcon, MatIconModule } from '@angular/material/icon';
import { MatDialog } from '@angular/material/dialog';
import { FormsModule } from '@angular/forms';
import { ProductQuery, CatalogApi } from '@shared/service/catalog-api';
import { PagedResponse } from '../models/page-response';
import { Product } from '../models/product';
import { ProductCard } from '../product-card/product-card';
import { FilterPanel } from '../filter-panel/filter-panel';
import { ProductFilterState } from '../service/product-filter-state';
import { ProductPaginationState } from '../service/product-pagination-state';
import { FilterChips } from '../filter-chips/filter-chips';
import { MatProgressSpinner } from '@angular/material/progress-spinner';
import { FilterDialog } from '../filter-dialog/filter-dialog';

@Component({
  selector: 'app-products',
  imports: [
    ProductCard,
    NgClass,
    MatPaginator,
    MatIconButton,
    MatIcon,
    MatIconModule,
    FormsModule,
    FilterPanel,
    FilterChips,
    MatProgressSpinner,
    MatButton,
  ],
  host: {
    class: 'min-h-screen',
  },
  template: `
    <div class="flex flex-col p-4 gap-4 md:px-20 md:pt-8">
      @if (!isMobile()) {
        <app-filter-panel />
      }
      <div
        class="flex flex-col gap-4 md:p-6 md:rounded-2xl md:border md:border-[#F0EEF0] md:bg-white"
      >
        <div class="flex justify-between items-center">
          <span class="font-semibold text-lg md:text-[24px]">Products</span>
          @if (isMobile()) {
            <button matIconButton class="btn-filter" (click)="openFilterDialog()">
              <mat-icon>filter_list</mat-icon>
            </button>
          }
        </div>

        <app-filter-chips />

        @if (pagedResult.isLoading()) {
          <div class="flex justify-center items-center py-12">
            <mat-progress-spinner mode="indeterminate" diameter="40" />
          </div>
        } @else if (pagedResult.error()) {
          <div class="flex flex-col items-center gap-3 py-12">
            <span class="text-gray-500">Failed to load products. Please try again.</span>
            <button matButton="filled" (click)="retry()">Retry</button>
          </div>
        } @else {
          <div [ngClass]="isMobile() ? 'responsive-grid' : 'md-responsive-grid'">
            @for (product of pagedResult.value().content; track product.id) {
              <app-product-card [product]="product" />
            }
          </div>

          <div class="flex items-center justify-center">
            <mat-paginator
              [length]="pagedResult.value().totalElements"
              [pageSize]="pageSize()"
              [pageIndex]="pageIndex()"
              aria-label="Select page"
              (page)="catchPagingInfo($event)"
            ></mat-paginator>
          </div>
        }
      </div>
    </div>
  `,
})
export default class ProductsPage {
  private breakpointObserver = inject(BreakpointObserver);
  private filterState = inject(ProductFilterState);
  private productService = inject(CatalogApi);

  isMobile = signal(false);
  matDialog = inject(MatDialog);

  filters = this.filterState.filters;

  constructor() {
    this.breakpointObserver
      .observe('(max-width: 768px)')
      .pipe(takeUntilDestroyed())
      .subscribe((result) => this.isMobile.set(result.matches));
  }

  openFilterDialog() {
    this.matDialog.open(FilterDialog);
  }

  pagedResult = rxResource<PagedResponse<Product>, ProductQuery>({
    params: () => {
      const filters = this.filters();
      return {
        page: this.pageIndex(),
        size: this.pageSize(),
        categories: filters.categories,
        minPrice: filters.minPrice ?? undefined,
        maxPrice: filters.maxPrice ?? undefined,
        isBestSeller: filters.isBestSeller ?? undefined,
        saleEvent: filters.saleEvent ?? undefined,
      };
    },
    stream: ({ params }) => this.productService.getProducts(params),
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

  private paginationState = inject(ProductPaginationState);
  pageIndex = this.paginationState.pageIndex;
  pageSize = this.paginationState.pageSize;

  catchPagingInfo(pageEvent: PageEvent) {
    this.paginationState.setPage(pageEvent.pageIndex, pageEvent.pageSize);
  }
}
