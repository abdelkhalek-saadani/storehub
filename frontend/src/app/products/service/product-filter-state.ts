import { Injectable, inject, computed } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { ActivatedRoute, Router } from '@angular/router';
import { ProductFilters } from '../models/product-filters';

@Injectable({ providedIn: 'root' })
export class ProductFilterState {
  private route = inject(ActivatedRoute);
  private router = inject(Router);

  private queryParamMap = toSignal(this.route.queryParamMap, {
    requireSync: true,
  });

  filters = computed<ProductFilters>(() => {
    const params = this.queryParamMap();
    const categories = params.get('categories');
    const minPrice = params.get('minPrice');
    const maxPrice = params.get('maxPrice');

    return {
      categories: categories ? categories.split(',') : [],
      minPrice: minPrice != null ? Number(minPrice) : null,
      maxPrice: maxPrice != null ? Number(maxPrice) : null,
    };
  });

  private patch(params: Record<string, string | null>) {
    this.router.navigate([], {
      relativeTo: this.route,
      queryParams: { ...params, page: '0' },
      queryParamsHandling: 'merge',
      replaceUrl: true,
    });
  }

  setCategories(categories: string[]) {
    this.patch({ categories: categories.length ? categories.join(',') : null });
  }

  setCategory(category: string) {
    this.patch({ categories: category });
  }

  removeCategory(category: string) {
    const remaining = this.filters().categories.filter((c) => c !== category);
    this.setCategories(remaining);
  }

  setPrice(minPrice: number | null, maxPrice: number | null) {
    this.patch({
      minPrice: minPrice ? String(minPrice) : null,
      maxPrice: maxPrice ? String(maxPrice) : null,
    });
  }

  clearMinPrice() {
    this.patch({ minPrice: null });
  }

  clearMaxPrice() {
    this.patch({ maxPrice: null });
  }
}
