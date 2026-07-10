import { Component, computed, inject } from '@angular/core';
import { ProductFilterState } from '../service/product-filter-state';
import { MatChip, MatChipRemove, MatChipSet } from '@angular/material/chips';
import { MatIcon } from '@angular/material/icon';

@Component({
  selector: 'app-filter-chips',
  imports: [MatChipSet, MatChip, MatChipRemove, MatIcon],
  template: `
    <mat-chip-set>
      @for (filter of filterChips(); track filter.label) {
        <mat-chip removable (removed)="remove(filter)">
          {{ filter.label }}
          <button matChipRemove>
            <mat-icon>cancel</mat-icon>
          </button>
        </mat-chip>
      }
    </mat-chip-set>
  `,
  styles: ``,
})
export class FilterChips {
  private filterState = inject(ProductFilterState);

  filters = this.filterState.filters;

  filterChips = computed<FilterChip[]>(() => {
    const f = this.filters();
    const chips: FilterChip[] = f.categories.map((c) => ({
      label: c,
      type: 'category',
      value: c,
    }));

    if (f.minPrice != null) {
      chips.push({ label: `Min: $${f.minPrice}`, type: 'minPrice' });
    }
    if (f.maxPrice != null) {
      chips.push({ label: `Max: $${f.maxPrice}`, type: 'maxPrice' });
    }

    return chips;
  });

  remove(chip: FilterChip) {
    switch (chip.type) {
      case 'category':
        this.filterState.removeCategory(chip.value);
        break;
      case 'minPrice':
        this.filterState.clearMinPrice();
        break;
      case 'maxPrice':
        this.filterState.clearMaxPrice();
        break;
    }
  }
}

type FilterChip =
  | { label: string; type: 'category'; value: string }
  | { label: string; type: 'minPrice' }
  | { label: string; type: 'maxPrice' };
