import { Component, input } from '@angular/core';
import { CopyText } from '../copy-text/copy-text';

@Component({
  selector: 'app-order-summary',
  imports: [CopyText],
  template: `
    <div class="flex flex-col gap-2 p-4 border border-[#F8F7F8] rounded-2xl bg-white">
      <span class="font-semibold text-base">Order Summary</span>
      <div class="flex items-center gap-2">
        <span class="text-[#807681] font-normal text-[14px]">Order Number</span>
        <app-copy-text [text]="orderNumber()" />
      </div>
      <div class="text-[14px] flex items-center justify-between py-3">
        <span class="font-normal">Items Total</span>
        <span class="font-medium">{{ itemsTotal() }} TND</span>
      </div>
      <div class="flex items-center justify-between py-3">
        <span class="font-normal text-[14px]">Promo Code</span>
        <span class="font-normal text -[18px] text-[#AB2538]">0 TND</span>
      </div>
      <div class="font-semibold text-[16px] flex items-center justify-between py-3">
        <span class="">Total</span>
        <span>{{ itemsTotal() }} TND</span>
      </div>
    </div>
  `,
  styles: ``,
})
export class OrderSummary {
  orderNumber = input.required<string>();
  itemsTotal = input.required<number>();
}
