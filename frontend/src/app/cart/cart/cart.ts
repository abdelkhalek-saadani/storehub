import { Component, computed, inject, OnInit, signal } from '@angular/core';
import { CartItem } from '@components/molecules/cart-item/cart-item';
import { MatButton, MatIconButton } from '@angular/material/button';
import { MatIcon } from '@angular/material/icon';
import { BreakpointObserver } from '@angular/cdk/layout';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { CartSidenav } from '@shared/service/cart-sidenav';
import { CartStore } from '../cart-store';
import { RouterLink } from '@angular/router';
import { StoreContext } from '../../products/service/store-context';

@Component({
  selector: 'app-cart',
  imports: [CartItem, MatButton, MatIcon, MatIconButton, RouterLink],
  template: `
    <div class="p-4 flex flex-col gap-5">
      <div class="flex justify-between">
        <div class="flex items-center gap-2">
          <span class="text-[24px] font-medium">My Cart</span>
          <div
            class="flex items-center justify-center rounded-full bg-primary w-[26px] aspect-square"
          >
            <span class="text-[14px] font-medium text-white">{{ store.itemCount() }}</span>
          </div>
        </div>
        <button matIconButton class="btn-close" (click)="cartSidenavService.close()">
          <mat-icon>close</mat-icon>
        </button>
      </div>
      <div class="flex flex-col p-4 gap-4">
        <div class="flex justify-between items-center">
          <span class="text-[18px] font-semibold text-[#7B7B7B]">Items</span>
          <div class="flex flex-row gap-3 items-center">
            <span class="text-[18px] font-semibold">{{ store.finalTotal() }} TND</span>
            @if (store.finalTotal() != store.originalTotal()) {
              <div class="text-xl font-regular text-[#A2A2A2] line-through">
                {{ store.originalTotal() }} TND
              </div>
            }
          </div>
        </div>
        <div class="flex flex-col gap-2 divide-y divide-[#F8F7F8]">
          @for (item of sortedItems(); track item.itemId) {
            <app-cart-item [item]="item" />
          }
        </div>
      </div>
      @if (isMobile()) {
        <button matButton="elevated" class="w-full">
          Continue Checkout
          <mat-icon iconPositionEnd>arrow_forward</mat-icon>
        </button>
      } @else {
        <button
          matButton="filled"
          class="btn-lg"
          [routerLink]="['/store', this.storeContext.storeSlug(), 'checkout']"
        >
          Continue Checkout
          <mat-icon iconPositionEnd>arrow_forward</mat-icon>
        </button>
      }
    </div>
  `,
  styles: ``,
})
export class Cart implements OnInit {
  isMobile = signal(false);
  cartSidenavService = inject(CartSidenav);
  storeContext = inject(StoreContext);
  protected store = inject(CartStore);

  items = this.store.items;

  sortedItems = computed(() =>
    [...this.store.items()].sort((a, b) => a.productId.localeCompare(b.productId)),
  );

  ngOnInit(): void {
    this.store.loadCart();
  }

  constructor(bpo: BreakpointObserver) {
    bpo
      .observe('(max-width: 768px)')
      .pipe(takeUntilDestroyed())
      .subscribe((res) => this.isMobile.set(res.matches));
  }
}
