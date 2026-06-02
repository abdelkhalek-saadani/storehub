import { Component } from '@angular/core';
import { MatButton } from '@angular/material/button';
import { MatIcon } from '@angular/material/icon';
import { MatProgressBar } from '@angular/material/progress-bar';

@Component({
  selector: 'app-checkout-details',
  imports: [MatButton, MatIcon, MatProgressBar],
  template: `
    <div class="p-8 flex flex-col gap-4 rounded-2xl border border-[#F0EEF0] max-w-96">
      <div class="flex flex-col gap-6">
        <div class="flex flex-col gap-3">
          <mat-progress-bar mode="determinate" value="50"></mat-progress-bar>
          <span class="font-medium text-sm">Free Delivery</span>
        </div>
        <div class="flex flex-col gap-4 pb-4 border-b border-[#F8F7F8]">
          <div class="font-medium text-lg">Order Summary</div>
          <div>
            <div class="flex flex-row justify-between">
              <span class="font-normal text-base text-[#686069]">Delivery fee</span
              ><span class="font-medium text-base text-black-900">0.00TND</span>
            </div>
            <div class="flex flex-row justify-between">
              <span class="font-normal text-base text-[#686069]">Items total</span
              ><span class="font-medium text-base text-black-900">17.00TND</span>
            </div>
          </div>
        </div>
        <div class="border-b border-[#F8F7F8] pb-4 flex flex-col gap-4">
          <div class="font-medium text-lg">Delivery Mode</div>
          <div class="flex flex-row justify-between items-center gap-4 text-[#08B772]">
            <span class=" font-medium text-lg">Home Delivery</span>
            <mat-icon>home</mat-icon>
          </div>
        </div>
        <div class="py-4 flex border-b border-t border-[#F8F7F8] justify-between items-center">
          <div class="font-medium text-base text-black-900">Coupon</div>
          <button matButton="text">
            <mat-icon>add</mat-icon>
            Add Coupon
          </button>
        </div>
        <div class="flex flex-row justify-between font-semibold text-xl text-black-900">
          <span>Total</span>
          <span>17.00TND</span>
        </div>
        <div class="text-[#807681] font-normal text-sm">
          By placing this order, you are agreeing to Terms and Conditions.
        </div>
      </div>
      <button matButton="filled" class="w-full btn-pill space-between">
        <!--<span style="display:flex; align-items:center; gap:8px;">-->
        <span class="flex items-center gap-2">
          <mat-icon>payment</mat-icon>
          Checkout
        </span>
        <span>$120.00</span>
      </button>
    </div>
  `,
  styles: ``,
})
export class CheckoutDetails {}
