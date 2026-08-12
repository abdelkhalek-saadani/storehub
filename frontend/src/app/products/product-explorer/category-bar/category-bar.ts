import { Component, computed, inject, signal } from '@angular/core';
import { CategorySquaredButton } from '../category-bar-squared-button/category-squared-button';
import { MatChip, MatChipAvatar, MatChipSet } from '@angular/material/chips';
import { BreakpointObserver } from '@angular/cdk/layout';
import { rxResource, takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { CategoryResponse, CatalogApi } from '@shared/service/catalog-api';

@Component({
  selector: 'app-category-bar',
  imports: [CategorySquaredButton, MatChip, MatChipAvatar],
  template: `
    <div
      class="flex flex-col items-stretch p-4 md:flex-row md:justify-center md:p-3
        md:p-3 md:rounded-lg md:border md:border-[#F0EEF0] md:bg-white md:my-6"
    >
      <div class="flex gap-2 justify-around md:gap-3 w-full">
        @for (category of categoriesFirst(); track category.id) {
          @if (isSquaredButton()) {
            <app-category-squared-button [category]="category" />
          } @else {
            <div
              class="cursor-pointer flex justify-center items-center bg-[#F8F7F8] w-full rounded-full"
            >
              <mat-chip class="category-chip">
                <img matChipAvatar [src]="category.imageUrl" [alt]="'Photo of ' + category.name" />
                {{ category.name }}
              </mat-chip>
            </div>
          }
        }
      </div>
      <div class="flex gap-2 justify-around md:gap-3 w-full">
        @for (category of categoriesSecond(); track $index) {
          @if (isSquaredButton()) {
            <app-category-squared-button [category]="category" />
          } @else {
            <div
              class="cursor-pointer flex justify-center items-center bg-[#F8F7F8] w-full rounded-full"
            >
              <mat-chip class="category-chip">
                <img matChipAvatar [src]="category.imageUrl" [alt]="'Photo of ' + category.name" />
                {{ category.name }}
              </mat-chip>
            </div>
          }
        }
      </div>
    </div>
  `,
  styles: ``,
})
export class CategoryBar {
  isSquaredButton = signal(false);
  catalogApi = inject(CatalogApi);

  constructor(bpo: BreakpointObserver) {
    bpo
      .observe('(max-width: 1040px)')
      .pipe(takeUntilDestroyed())
      .subscribe((result) => this.isSquaredButton.set(result.matches));
  }

  categories = rxResource<CategoryResponse[], { count: number }>({
    params: () => ({ count: 8 }),
    stream: ({ params }) => this.catalogApi.getCategories(params.count),
  });

  categoriesFirst = computed(() => (this.categories.value() ?? []).slice(0, 4));

  categoriesSecond = computed(() => (this.categories.value() ?? []).slice(4, 8));
}
