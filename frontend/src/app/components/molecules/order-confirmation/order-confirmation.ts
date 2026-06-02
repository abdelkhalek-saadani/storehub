import { Component, input } from '@angular/core';
import { MatButton } from '@angular/material/button';
import { MatIcon } from '@angular/material/icon';

@Component({
  selector: 'app-order-confirmation',
  imports: [MatButton, MatIcon],
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
      <button matButton="outlined" class="w-52">
        Go back home
        <mat-icon iconPositionEnd>arrow_forward</mat-icon>
      </button>
    </div>
  `,
  styles: ``,
})
export class OrderConfirmation {
  state = input<'fail' | 'success'>('success');
}
