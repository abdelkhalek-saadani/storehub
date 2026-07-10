import { Component, effect, inject, signal } from '@angular/core';
import { MatButton } from '@angular/material/button';
import { MatCheckbox } from '@angular/material/checkbox';
import { MatFormField } from '@angular/material/form-field';
import { MatIcon } from '@angular/material/icon';
import { MatInput, MatLabel } from '@angular/material/input';
import { MatMenu, MatMenuTrigger } from '@angular/material/menu';
import { ProductFilterState } from '../service/product-filter-state';

@Component({
  selector: 'app-filter-panel',
  imports: [
    MatButton,
    MatCheckbox,
    MatFormField,
    MatIcon,
    MatInput,
    MatLabel,
    MatLabel,
    MatMenu,
    MatMenuTrigger,
    MatButton,
    MatMenuTrigger,
    MatMenu,
    MatCheckbox,
    MatFormField,
    MatInput,
  ],
  template: `
    <div class="p-6 flex gap-4 rounded-2xl border border-[#F0EEF0] bg-white">
      <button matButton="filled" class="btn-filter" [matMenuTriggerFor]="categoryMenu">
        Category
        <mat-icon iconPositionEnd>expand_more</mat-icon>
      </button>

      <mat-menu #categoryMenu>
        <div class="px-4 py-2 flex flex-col gap-2" (click)="$event.stopPropagation()">
          <span class="text-sm text-gray-500">Filter by category</span>

          @for (category of categories; track $index) {
            <mat-checkbox
              [checked]="selectedCategories().includes(category)"
              (change)="toggleCategory(category, $event.checked)"
            >
              {{ category }}
            </mat-checkbox>
          }

          <div class="flex gap-2 pt-2">
            <button matButton (click)="applyCategoriesFilter()">Apply</button>
            <button matButton (click)="resetCategoriesFilter()">Reset</button>
          </div>
        </div>
      </mat-menu>
      <button matButton="filled" class="btn-filter" [matMenuTriggerFor]="priceMenu">
        Price
        <mat-icon iconPositionEnd>expand_more</mat-icon>
      </button>

      <mat-menu #priceMenu>
        <div class="px-4 py-2 flex flex-col gap-3" (click)="$event.stopPropagation()">
          <span class="text-sm text-gray-500">Filter by price range</span>

          <div class="flex gap-2">
            <mat-form-field appearance="outline">
              <mat-label>Min</mat-label>
              <input
                [value]="minPrice()"
                (input)="onMinPriceInput($event)"
                matInput
                type="number"
              />
            </mat-form-field>

            <mat-form-field appearance="outline">
              <mat-label>Max</mat-label>
              <input
                [value]="maxPrice()"
                (input)="onMaxPriceInput($event)"
                matInput
                type="number"
              />
            </mat-form-field>
          </div>

          <div class="flex gap-2">
            <button matButton (click)="applyPriceFilter(minPrice(), maxPrice())">Apply</button>
            <button matButton (click)="resetPriceFilter()">Reset</button>
          </div>
        </div>
      </mat-menu>
    </div>
  `,
  styles: ``,
})
export class FilterPanel {
  categories = ['Dairy', 'Fresh', 'Organic', 'Frozen'];
  productFilterState = inject(ProductFilterState);
  minPrice = signal<number | null>(null);
  maxPrice = signal<number | null>(null);
  selectedCategories = signal<string[]>([]);
  initialized = false;

  constructor() {
    effect(() => {
      const filters = this.productFilterState.filters(); // subscribe to filter changes

      this.minPrice.set(filters.minPrice);
      this.maxPrice.set(filters.maxPrice);
      this.selectedCategories.set(filters.categories);
    });
  }

  toggleCategory(category: string, checked: boolean) {
    this.selectedCategories.update((current) =>
      checked ? [...current, category] : current.filter((c) => c !== category),
    );
  }

  applyPriceFilter(minPrice: number | null, maxPrice: number | null) {
    this.productFilterState.setPrice(minPrice, maxPrice);
  }

  resetPriceFilter() {
    this.productFilterState.setPrice(null, null);
  }

  applyCategoriesFilter() {
    this.productFilterState.setCategories(this.selectedCategories());
  }

  resetCategoriesFilter() {
    this.productFilterState.setCategories([]);
  }

  onMinPriceInput(event: Event) {
    const value = (event.target as HTMLInputElement).value;
    this.minPrice.set(value ? Number(value) : null);
  }

  onMaxPriceInput(event: Event) {
    const value = (event.target as HTMLInputElement).value;
    this.maxPrice.set(value ? Number(value) : null);
  }
}
