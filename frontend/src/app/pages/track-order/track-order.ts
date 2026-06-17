import { Component, signal } from '@angular/core';
import { OrderTracking } from '@components/molecules/order-tracking/order-tracking';
import { ReviewOrder } from '@components/molecules/review-order/review-order';
import { MatButton, MatIconButton } from '@angular/material/button';
import { MatIcon } from '@angular/material/icon';
import { CopyText } from '@components/atoms/copy-text/copy-text';
import { PaymentSummary } from './payment-summary/payment-summary';
import { LocationSummary } from './location-summary/location-summary';
import { OrderSummary } from './order-summary/order-summary';
import { InvoiceDownload } from './invoice-download/invoice-download';
import { BreakpointObserver } from '@angular/cdk/layout';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { sign } from 'node:crypto';

@Component({
  selector: 'app-track-order',
  imports: [
    OrderTracking,
    ReviewOrder,
    MatButton,
    MatIcon,
    MatIconButton,
    CopyText,
    PaymentSummary,
    LocationSummary,
    OrderSummary,
    InvoiceDownload,
  ],
  host: {
    class: 'min-h-screen flex flex-col px-4 pb-10 bg-[#FEFCFE] ',
  },
  template: `
    <div class="flex flex-col gap-4">
      <div class="py-5 flex items-center gap-2">
        <button matIconButton class="back-button">
          <mat-icon>arrow_back</mat-icon>
        </button>
        <span class="font-semibold text-[20px]">Order Details</span>
      </div>

      @if (isMobile()) {
        <app-order-tracking />
        <app-payment-summary />
        <app-location-summary />
        <app-order-summary />
        <app-review-order />
        <app-invoice-download />
      } @else {
        <div class="flex gap-5">
          <div class="flex flex-col gap-6 w-2/3">
            <app-order-tracking />
            <app-review-order />
          </div>
          <div class="flex flex-col gap-4 w-1/3">
            <app-order-summary />
            <app-payment-summary />
            <app-location-summary />
            <app-invoice-download />
          </div>
        </div>
      }

      <div
        class=" md:mt-8 p-6 md:p-8 flex flex-col md:flex-row md:justify-between items-center gap-4 border-[#F8F7F8] rounded-2xl bg-white"
      >
        <span class="font-medium text-[14px] md:text-base"
          >You can can your order before its prepared</span
        >
        <button matButton="filled" class="danger w-full md:w-auto">Cancel Order</button>
      </div>
    </div>
  `,
})
export default class TrackOrderPage {
  isMobile = signal(false);

  constructor(bpo: BreakpointObserver) {
    bpo
      .observe('(max-width: 768px)')
      .pipe(takeUntilDestroyed())
      .subscribe((res) => this.isMobile.set(res.matches));
  }
}
