import { Component, inject, input } from '@angular/core';
import { MatButton } from '@angular/material/button';
import { MatIcon } from '@angular/material/icon';
import { Router, RouterLink } from '@angular/router';
import { StoreContext } from '../../products/service/store-context';

@Component({
  selector: 'app-order-confirmation',
  imports: [MatButton, MatIcon, RouterLink],
  template: `
    <div class="flex flex-col w-fit items-center">
      @if (state() == 'success') {
        <div class="h-54 w-74 mb-6">
          <img src="success-order.png" alt="Order success image" />
        </div>
        <span class="font-semibold text-2xl text-black mb-3 text-center"
          >Order Placed Successfully!</span
        >
        <span class="mb-8 text-[#8B8F99] text-lg font-normal text-center"
          >Go back home and track your order</span
        >
      } @else {
        <div class="h-54 w-74 mb-6">
          <img src="failed-order.png" alt="Order fails image" />
        </div>
        <span class="font-semibold text-2xl text-black mb-3 text-center">Order Failed!</span>
        <span class="mb-8 text-[#8B8F99] text-lg font-normal text-center"
          >Go back home and try again</span
        >
      }
      <button matButton="outlined" class="w-52" (click)="goBackHome()">
        Go back home
        <mat-icon iconPositionEnd>arrow_forward</mat-icon>
      </button>
    </div>
  `,
  styles: ``,
})
export class OrderConfirmation {
  state = input<'fail' | 'success'>('success');
  storeContext = inject(StoreContext);
  router = inject(Router);

  goBackHome() {
    const currentStore = this.storeContext.getCurrentStore();
    if (currentStore) {
      this.router.navigate(['/store', currentStore.slug, 'products']);
      return;
    }
    // TODO: implement store pick page
    this.router.navigate(['store']);
  }
}
