import { Component, computed, inject, signal } from '@angular/core';
import { MatFormFieldModule } from '@angular/material/form-field';

import { FormsModule } from '@angular/forms';
import { DatePipe } from '@angular/common';
import { MatIcon } from '@angular/material/icon';
import { BreakpointObserver } from '@angular/cdk/layout';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { CheckoutForm } from './checkout-form/checkout-form';
import { CheckoutDetails } from './checkout-details/checkout-details';
import { ReviewOrder } from '@shared/components/review-order/review-order';
import { CartStore } from '../cart/cart-store';

@Component({
  selector: 'app-checkout',
  imports: [
    FormsModule,
    MatFormFieldModule,
    DatePipe,
    MatIcon,
    ReviewOrder,
    CheckoutDetails,
    CheckoutForm,
    CheckoutForm,
    CheckoutDetails,
  ],
  host: {
    class:
      'min-h-screen flex flex-col md:flex-row gap-6 md:gap-12 px-6 md:pt-10 md:px-20 bg-[#FEFCFE]',
  },
  template: `
    <div
      class="md:w-2/3 flex flex-col gap-6 md:p-6 md:gap-4 md:bg-white md:border md:border-[#F0EEF0] md:rounded-xl"
    >
      <div class="flex items-center mt-3">
        <div class="flex gap-2 items-center text-lg md:text-[22px] font-semibold">
          <div class="text-3xl text-primary leading-8">
            <mat-icon [inline]="true">shopping_cart_checkout</mat-icon>
          </div>
          <span class="">Checkout</span>
        </div>
      </div>

      <app-checkout-form />

      <app-review-order [items]="cartItems()" />
    </div>
    <app-checkout-details class="md:w-1/3" />
  `,
})
export default class CheckoutPage {
  isMobile = signal(false);
  cartStore = inject(CartStore);

  cartItems = computed(() => this.cartStore.items());

  constructor(bpo: BreakpointObserver) {
    bpo
      .observe('(max-width: 768px)')
      .pipe(takeUntilDestroyed())
      .subscribe((res) => this.isMobile.set(res.matches));
  }
}
