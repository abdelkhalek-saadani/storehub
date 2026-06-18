import { Component, inject, signal } from '@angular/core';
import { CartItem } from '@components/molecules/cart-item/cart-item';
import { MatButton, MatIconButton } from '@angular/material/button';
import { MatIcon } from '@angular/material/icon';
import { BreakpointObserver } from '@angular/cdk/layout';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { CartSidenav } from '../../services/cart-sidenav';

@Component({
  selector: 'app-cart',
  imports: [CartItem, MatButton, MatIcon, MatIconButton],
  template: `
    <div class="p-4 flex flex-col gap-5">
      <div class="flex justify-between">
        <div class="flex items-center gap-2">
          <span class="text-[24px] font-medium">My Cart</span>
          <div
            class="flex items-center justify-center rounded-full bg-primary w-[26px] aspect-square"
          >
            <span class="text-[14px] font-medium text-white">4</span>
          </div>
        </div>
        <button matIconButton class="btn-close" (click)="cartSidenavService.close()">
          <mat-icon>close</mat-icon>
        </button>
      </div>
      <div class="flex flex-col p-4 gap-4">
        <div class="flex justify-between items-center">
          <span class="text-[18px] font-semibold text-[#7B7B7B]">Items Ordered</span>
          <span class="text-[18px] font-semibold">17.00TND</span>
        </div>
        <div class="flex flex-col gap-2 divide-y divide-[#F8F7F8]">
          @for (i of [].constructor(4); track $index) {
            <app-cart-item />
          }
        </div>
      </div>
      @if (isMobile()) {
        <button matButton="elevated" class="w-full">
          Continue Checkout
          <mat-icon iconPositionEnd>arrow_forward</mat-icon>
        </button>
      } @else {
        <button matButton="filled" class="btn-lg">
          Continue Checkout
          <mat-icon iconPositionEnd>arrow_forward</mat-icon>
        </button>
      }
    </div>
  `,
  styles: ``,
})
export class Cart {
  isMobile = signal(false);
  cartSidenavService = inject(CartSidenav);

  constructor(bpo: BreakpointObserver) {
    bpo
      .observe('(max-width: 768px)')
      .pipe(takeUntilDestroyed())
      .subscribe((res) => this.isMobile.set(res.matches));
  }
}
