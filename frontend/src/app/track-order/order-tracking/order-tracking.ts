import { Component, computed, input, signal } from '@angular/core';
import { MatIcon } from '@angular/material/icon';
import { BreakpointObserver } from '@angular/cdk/layout';
import { Breakpoints } from '@core/constants/breakpoints';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { OrderStatusDto } from '@shared/service/order-api';
import { DatePipe, NgClass } from '@angular/common';
import { LocalDateTime } from '@js-joda/core';

@Component({
  selector: 'app-order-tracking',
  imports: [MatIcon, NgClass, DatePipe],
  template: `
    <div class="border border-[#F8F7F8] rounded-2xl p-4 flex flex-col gap-4">
      <div class="flex items-center justify-between">
        <div class="flex flex-col gap-1">
          <span class="text-lg md:text-[22px] font-semibold text-black-900" data-cy="order-status">
            {{ status().label }}
          </span>
          @if (!isOrderFailed() && status().code != 'DELIVERED') {
            <span class="text-sm font-medium text-primary">Order {{ orderArriveIn() }}</span>
          }
        </div>
        <div
          [ngClass]="isOrderFailed() ? 'bg-red-500/6 text-red-500' : 'bg-primary/6 text-primary'"
          class="py-1 px-2 md:py-2 md:px-3 flex align-center justify-center font-medium text-sm border border-primary rounded-3xl"
        >
          {{ status().label }}
        </div>
      </div>
      <div class="flex flex-col items-center">
        <div class="pb-4">
          <div class="p-5 rounded-full bg-[#CAF5CA] flex items-center justify-center">
            <div
              class=" h-[24px] w-[24px] text-[24px] leading-[24px] md:h-[42px] md:w-[42px] md:text-[42px] md:leading-[42px]"
              [ngClass]="isOrderFailed() ? 'text-red-500' : 'text-[#00BA00]'"
            >
              @if (isOrderFailed()) {
                <mat-icon [inline]="true">error</mat-icon>
              } @else {
                <mat-icon [inline]="true">check_circle</mat-icon>
              }
            </div>
          </div>
        </div>
        <span class="text-base md:text-xl font-semibold pb-1">{{ status().label }}</span>
        <span class="text-sm font-normal text-[#807681] pb-6">
          {{ createdAt() | date: 'MMM d, y, h:mm a' }}</span
        >
        <div class="flex justify-evenly gap-4 md:gap-8 w-full rounded-2xl">
          <div class="w-full flex flex-col gap-4">
            <div
              class="h-2 rounded-2xl w-full"
              [ngClass]="isFirstStepChecked() ? 'bg-primary' : 'bg-[#F8F7F8]'"
            ></div>
            @if (isMdDevice()) {
              <div class="ps-4 flex gap-2 items-center">
                @if (isFirstStepChecked()) {
                  <div class="text-primary h-[16px] w-[16px] text-[16px] leading-[16px]">
                    <mat-icon [inline]="true">check_circle</mat-icon>
                  </div>
                }
                <span class="text-sm font-normal">
                  @if (
                    status().code == 'CREATED' ||
                    status().code == 'AWAITING_PAYMENT' ||
                    status().code == 'PROCESSING_PAYMENT' ||
                    isSecondStepChecked() ||
                    isThirdStepChecked()
                  ) {
                    {{ 'Paid' }}
                  }
                </span>
              </div>
            }
          </div>
          <div class="w-full flex flex-col gap-4">
            <div
              class="h-2 rounded-2xl w-full"
              [ngClass]="isSecondStepChecked() ? 'bg-primary' : 'bg-[#F8F7F8]'"
            ></div>
            @if (isMdDevice()) {
              <div class="ps-4 flex gap-2 items-center">
                @if (isSecondStepChecked()) {
                  <div class="text-primary h-[16px] w-[16px] text-[16px] leading-[16px]">
                    <mat-icon [inline]="true">check_circle</mat-icon>
                  </div>
                }
                <span class="text-sm font-normal">
                  @if (isSecondStepChecked()) {
                    {{ 'Shipped' }}
                  }
                </span>
              </div>
            }
          </div>
          <div class="w-full flex flex-col gap-4">
            <div
              class="h-2 rounded-2xl w-full"
              [ngClass]="isThirdStepChecked() ? 'bg-primary' : 'bg-[#F8F7F8]'"
            ></div>
            @if (isMdDevice()) {
              <div class="ps-4 flex gap-2 items-center">
                <div class="h-[16px] w-[16px] rounded-full border border-[#F0EEF0]"></div>
                @if (isThirdStepChecked()) {
                  <div class="text-primary h-[16px] w-[16px] text-[16px] leading-[16px]">
                    <mat-icon [inline]="true">check_circle</mat-icon>
                  </div>
                }
                <span class="text-sm font-normal">
                  @if (isThirdStepChecked()) {
                    {{ status().label }}
                  }
                </span>
              </div>
            }
          </div>
        </div>
      </div>
    </div>
  `,
  styles: ``,
})
export class OrderTracking {
  isMdDevice = signal(false);
  status = input.required<OrderStatusDto>();
  createdAt = input.required<Date>();
  orderArriveIn = input.required();

  isOrderFailed = computed(() => {
    const s = this.status();
    return (
      !s || s.code == 'PAYMENT_FAILED' || s.code == 'PAYMENT_VOIDED' || s.code == 'PAYMENT_REFUNDED'
    );
  });

  isFirstStepChecked = computed(() => {
    const s = this.status();
    return (
      !s ||
      s.code == 'PAYMENT_AUTHORIZED' ||
      s.code == 'PAYMENT_CAPTURED' ||
      s.code == 'SHIPPED' ||
      s.code == 'DELIVERED'
    );
  });

  isSecondStepChecked = computed(() => {
    const s = this.status();
    return !s || s.code == 'SHIPPED' || this.isThirdStepChecked();
  });

  isThirdStepChecked = computed(() => {
    const s = this.status();
    return !s || s.code == 'DELIVERED';
  });

  constructor(bpo: BreakpointObserver) {
    bpo
      .observe(Breakpoints.md)
      .pipe(takeUntilDestroyed())
      .subscribe((result) => this.isMdDevice.set(result.matches));
  }
}
