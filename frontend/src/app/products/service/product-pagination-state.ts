import { Injectable, inject, computed } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { ActivatedRoute, Router } from '@angular/router';

@Injectable({ providedIn: 'root' })
export class ProductPaginationState {
  private route = inject(ActivatedRoute);
  private router = inject(Router);

  private queryParamMap = toSignal(this.route.queryParamMap, {
    requireSync: true,
  });

  pageIndex = computed(() => Number(this.queryParamMap().get('page') ?? 0));
  pageSize = computed(() => Number(this.queryParamMap().get('size') ?? 50));

  private patch(params: Record<string, string | null>) {
    this.router.navigate([], {
      relativeTo: this.route,
      queryParams: params,
      queryParamsHandling: 'merge',
      replaceUrl: true,
    });
  }

  setPage(pageIndex: number, pageSize: number) {
    this.patch({ page: String(pageIndex), size: String(pageSize) });
  }
}
