import { Component, inject } from '@angular/core';
import { rxResource, toSignal } from '@angular/core/rxjs-interop';
import { FormControl, ReactiveFormsModule } from '@angular/forms';
import { debounceTime, distinctUntilChanged, map } from 'rxjs';
import { CatalogApi } from '@shared/service/catalog-api';
import { Product } from '../../../products/models/product';
import { Router } from '@angular/router';
import {
  MatAutocomplete,
  MatAutocompleteOrigin,
  MatAutocompleteTrigger,
  MatOption,
} from '@angular/material/autocomplete';
import { MatIcon } from '@angular/material/icon';

@Component({
  selector: 'app-search-bar',
  imports: [
    ReactiveFormsModule,
    MatAutocompleteOrigin,
    MatIcon,
    MatAutocomplete,
    MatOption,
    MatAutocompleteTrigger,
  ],
  template: `
    <div
      matAutocompleteOrigin
      #origin="matAutocompleteOrigin"
      class="flex items-center gap-2 bg-[#F8F7F8] rounded-2xl p-4 h-12"
    >
      <div class="bg-red text-primary h-6 w-6 text-[24px] leading-[24px]">
        <mat-icon [inline]="true">search</mat-icon>
      </div>
      <input
        type="text"
        placeholder="Search..."
        class="bg-transparent outline-none text-sm text-gray-700 placeholder-gray-400 w-full"
        [formControl]="searchControl"
        [matAutocomplete]="auto"
        [matAutocompleteConnectedTo]="origin"
        aria-label="Search products"
      />
    </div>

    <mat-autocomplete #auto="matAutocomplete">
      @if (query()) {
        @if (results.isLoading()) {
          <mat-option disabled>Searching...</mat-option>
        } @else if (results.error()) {
          <mat-option disabled>Something went wrong. Try again.</mat-option>
        } @else {
          @for (product of results.value(); track product.id) {
            <mat-option [value]="product.name" (click)="navigateTo(product)" )>
              <div class="flex items-center gap-3 py-1">
                <img
                  [src]="product.imageUrl"
                  [alt]="product.name"
                  width="40"
                  height="40"
                  loading="lazy"
                  class="h-10 w-10 rounded-lg object-cover"
                />
                <div class="min-w-0">
                  <div class="text-sm font-medium truncate">{{ product.name }}</div>
                  <div class="text-xs text-gray-500 truncate">{{ product.description }}</div>
                </div>
              </div>
            </mat-option>
          } @empty {
            <mat-option disabled>No results found</mat-option>
          }
        }
      }
    </mat-autocomplete>
  `,
})
export class SearchBar {
  private catalogApi = inject(CatalogApi);
  private router = inject(Router);

  readonly searchControl = new FormControl<string>('', { nonNullable: true });

  readonly query = toSignal(
    this.searchControl.valueChanges.pipe(
      map((v) => v.trim()),
      debounceTime(300),
      distinctUntilChanged(),
    ),
    { initialValue: '' },
  );

  readonly results = rxResource({
    params: () => this.query() || undefined, // idle when empty
    stream: ({ params: q }) => this.catalogApi.searchProducts(q),
  });

  navigateTo(product: Product) {
    this.router.navigate(['/product', product.id]);
  }
}
