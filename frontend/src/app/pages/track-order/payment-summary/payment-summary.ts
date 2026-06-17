import { Component } from '@angular/core';
import { MatIcon } from '@angular/material/icon';

@Component({
  selector: 'app-payment-summary',
  imports: [MatIcon],
  template: `
    <div class="flex flex-col gap-2 p-4 border border-[#F8F7F8] rounded-2xl bg-white">
      <span class="text-base font-semibold">Pay With</span>
      <div class="text-primary text-[12px] flex items-center gap-1">
        <mat-icon>credit_card</mat-icon>
        <span class="font-medium">Paypal</span>
      </div>
    </div>
  `,
  styles: ``,
})
export class PaymentSummary {}
