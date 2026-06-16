import { Component, inject, signal } from '@angular/core';
import { ProductCard } from '@components/molecules/product-card/product-card';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { BreakpointObserver } from '@angular/cdk/layout';
import { NgClass } from '@angular/common';
import { MatPaginator, PageEvent } from '@angular/material/paginator';
import { MatButton, MatIconButton } from '@angular/material/button';
import { MatIcon, MatIconModule } from '@angular/material/icon';
import { MatChip, MatChipRemove, MatChipSet } from '@angular/material/chips';
import { MatDialog } from '@angular/material/dialog';
import { FilterDialog } from '@components/filter-dialog/filter-dialog';
import { MatMenu, MatMenuTrigger } from '@angular/material/menu';
import { MatCheckbox } from '@angular/material/checkbox';
import { MatFormField } from '@angular/material/form-field';
import { MatInput } from '@angular/material/input';
import { FormsModule } from '@angular/forms';
import { MatLabel } from '@angular/material/form-field';

@Component({
  selector: 'app-products',
  imports: [
    ProductCard,
    NgClass,
    MatPaginator,
    MatIconButton,
    MatIcon,
    MatChipSet,
    MatChip,
    MatIconModule,
    MatChipRemove,
    MatButton,
    MatMenuTrigger,
    MatMenu,
    MatCheckbox,
    MatFormField,
    MatInput,
    FormsModule,
    MatLabel,
  ],
  host: {
    class: 'min-h-screen',
  },
  template: `
    <div class="flex flex-col p-4 gap-4 md:px-20 md:pt-8">
      @if (!isMobile()) {
        <div class="p-6 flex gap-4 rounded-2xl border border-[#F0EEF0] bg-white">
          <button matButton="filled" class="btn-filter" [matMenuTriggerFor]="categoryMenu">
            Category <mat-icon iconPositionEnd>expand_more</mat-icon>
          </button>

          <mat-menu #categoryMenu>
            <div class="px-4 py-2 flex flex-col gap-2" (click)="$event.stopPropagation()">
              <span class="text-sm text-gray-500">Filter by category</span>

              @for (category of categories; track category) {
                <mat-checkbox>
                  {{ category }}
                </mat-checkbox>
              }

              <div class="flex gap-2 pt-2">
                <button matButton>Apply</button>
                <button matButton>Reset</button>
              </div>
            </div>
          </mat-menu>
          <button matButton="filled" class="btn-filter" [matMenuTriggerFor]="priceMenu">
            Price <mat-icon iconPositionEnd>expand_more</mat-icon>
          </button>

          <mat-menu #priceMenu>
            <div class="px-4 py-2 flex flex-col gap-3" (click)="$event.stopPropagation()">
              <span class="text-sm text-gray-500">Filter by price range</span>

              <div class="flex gap-2">
                <mat-form-field appearance="outline">
                  <mat-label>Min</mat-label>
                  <input matInput type="number" />
                </mat-form-field>

                <mat-form-field appearance="outline">
                  <mat-label>Max</mat-label>
                  <input matInput type="number" />
                </mat-form-field>
              </div>

              <div class="flex gap-2">
                <button matButton>Apply</button>
                <button matButton>Reset</button>
              </div>
            </div>
          </mat-menu>
        </div>
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

        <mat-chip-set>
          @for (filter of filters(); track filter) {
            <mat-chip removable (removed)="remove(filter)">
              {{ filter }}
              <button matChipRemove>
                <mat-icon>cancel</mat-icon>
              </button>
            </mat-chip>
          }
        </mat-chip-set>

        <div [ngClass]="isMobile() ? 'responsive-grid' : 'md-responsive-grid'">
          @for (i of [].constructor(10); track $index) {
            <app-product-card class="max-w-none md:max-w-none" />
          }
        </div>

        <div class="flex items-center justify-center">
          <mat-paginator
            [length]="150"
            [pageSize]="50"
            aria-label="Select page"
            (page)="catchPagingInfo($event)"
          ></mat-paginator>
        </div>
      </div>
    </div>
  `,
})
export default class ProductsPage {
  private breakpointObserver = inject(BreakpointObserver);
  isMobile = signal(false);
  categories = ['Dairy', 'Fresh', 'Organic', 'Frozen'];
  filters = signal([
    'Dairy',
    'Fresh',
    'Organic',
    'Fresh',
    'Organic',
    'Fresh',
    'Organic',
    'Fresh',
    'Organic',
  ]);
  matDialog = inject(MatDialog);

  constructor() {
    this.breakpointObserver
      .observe('(max-width: 768px)')
      .pipe(takeUntilDestroyed())
      .subscribe((result) => this.isMobile.set(result.matches));
  }

  openFilterDialog() {
    this.matDialog.open(FilterDialog);
  }

  remove(filter: string) {
    this.filters.update((current) => current.filter((f) => f !== filter));
  }

  catchPagingInfo(pageEvent: PageEvent) {
    console.log(pageEvent.pageIndex);
  }
}
