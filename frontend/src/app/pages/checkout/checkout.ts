import { Component, signal } from '@angular/core';
import {
  MatDatepicker,
  MatDatepickerInput,
  MatDatepickerToggle,
} from '@angular/material/datepicker';
import { MatFormFieldModule, MatLabel } from '@angular/material/form-field';
import { MatInput, MatSuffix } from '@angular/material/input';
import {
  MatTimepicker,
  MatTimepickerInput,
  MatTimepickerToggle,
} from '@angular/material/timepicker';
import { FormsModule } from '@angular/forms';
import { DatePipe } from '@angular/common';
import { MatIcon } from '@angular/material/icon';
import { ReviewOrder } from '@components/molecules/review-order/review-order';
import { SummarizeOrder } from '@components/summarize-order/summarize-order';
import { CheckoutDetails } from '@components/molecules/checkout-details/checkout-details';
import { BreakpointObserver } from '@angular/cdk/layout';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { CheckoutForm } from '@components/molecules/checkout-form/checkout-form';

@Component({
  selector: 'app-checkout',
  imports: [
    MatDatepicker,
    MatLabel,
    MatDatepickerToggle,
    MatInput,
    MatDatepickerInput,
    MatTimepickerInput,
    FormsModule,
    MatTimepicker,
    MatTimepickerToggle,
    MatSuffix,
    MatFormFieldModule,
    DatePipe,
    MatIcon,
    ReviewOrder,
    CheckoutDetails,
    CheckoutForm,
  ],
  host: {
    class:
      'min-h-screen flex flex-col md:flex-row gap-6 md:gap-12 px-6 md:pt-10 md:px-20 bg-[#FEFCFE]',
  },
  template: `
    <div
      class="md:w-2/3 flex flex-col gap-6 md:p-6 md:gap-4 md:bg-white md:border md:border-[#F0EEF0] md:rounded-xl"
    >
      <div class="flex justify-between items-center mt-3">
        <div class="flex gap-2 items-center text-lg md:text-[22px] font-semibold">
          <div class="text-3xl text-primary leading-8">
            <mat-icon [inline]="true">shopping_cart_checkout</mat-icon>
          </div>
          <span class="">Checkout</span>
        </div>
        @if (value) {
          <div class="flex gap-1 items-center text-primary text-base">
            <mat-icon [inline]="true"> date_range</mat-icon>
            <span>{{ value | date: 'MMM d, h a' }}</span>
          </div>
        }
      </div>
      <div class="flex gap-2 justify-center items-center">
        <mat-form-field>
          <mat-label>Delivery Date</mat-label>
          <input matInput [matDatepicker]="datepicker" [(ngModel)]="value" />
          <mat-datepicker #datepicker />
          <mat-datepicker-toggle [for]="datepicker" matSuffix />
        </mat-form-field>

        <mat-form-field>
          <mat-label>Time Slot</mat-label>
          <input
            matInput
            [matTimepicker]="timepicker"
            [(ngModel)]="value"
            [ngModelOptions]="{ updateOn: 'blur' }"
          />
          <mat-timepicker #timepicker />
          <mat-timepicker-toggle [for]="timepicker" matSuffix />
        </mat-form-field>
      </div>

      @if (!isMobile()) {
        <app-checkout-form />
      }
      <app-review-order />
    </div>
    <app-checkout-details class="md:w-1/3" />
  `,
})
export default class CheckoutPage {
  value!: Date;
  isMobile = signal(false);

  constructor(bpo: BreakpointObserver) {
    bpo
      .observe('(max-width: 768px)')
      .pipe(takeUntilDestroyed())
      .subscribe((res) => this.isMobile.set(res.matches));
  }
}
