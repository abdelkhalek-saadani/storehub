import { Component, signal } from '@angular/core';
import { MatIcon } from '@angular/material/icon';
import { BreakpointObserver } from '@angular/cdk/layout';
import { Breakpoints } from '@core/constants/breakpoints';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';

@Component({
  selector: 'app-order-tracking',
  imports: [MatIcon],
  template: `
    <div class="border border-[#F8F7F8] rounded-2xl p-4 flex flex-col gap-4">
      <div class="flex items-center justify-between">
        <div class="flex flex-col gap-1">
          <span class="text-lg md:text-[22px] font-semibold text-black-900">Order in Progress</span>
          <span class="text-sm font-medium text-primary">Order arrives in 2 hours</span>
        </div>
        <div
          class="bg-primary/6 py-1 px-2 md:py-2 md:px-3 flex align-center justify-center font-medium text-sm text-primary border border-primary rounded-3xl"
        >
          In Progress
        </div>
      </div>
      <div class="flex flex-col items-center">
        <div class="pb-4">
          <div class="p-5 rounded-full bg-[#CAF5CA] flex items-center justify-center">
            <div
              class="text-[#00BA00] h-[24px] w-[24px] text-[24px] leading-[24px] md:h-[42px] md:w-[42px] md:text-[42px] md:leading-[42px]"
            >
              <mat-icon [inline]="true">check_circle</mat-icon>
            </div>
          </div>
        </div>
        <span class="text-base md:text-xl font-semibold pb-1">Order is Placed</span>
        <span class="text-sm font-normal text-[#807681] pb-6">Apr 5, 2022, 10:07 AM </span>
        <div class="flex justify-evenly gap-4 md:gap-8 w-full rounded-2xl">
          <div class="w-full flex flex-col gap-4">
            <div class="h-2 rounded-2xl w-full bg-primary"></div>
            @if (isMdDevice()) {
              <div class="ps-4 flex gap-2 items-center">
                <div class="text-primary h-[16px] w-[16px] text-[16px] leading-[16px]">
                  <mat-icon [inline]="true">check_circle</mat-icon>
                </div>
                <span class="text-sm font-normal">Order Paid</span>
              </div>
            }
          </div>
          <div class="w-full flex flex-col gap-4">
            <div class="h-2 rounded-2xl w-full bg-[#F8F7F8]"></div>
            @if (isMdDevice()) {
              <div class="ps-4 flex gap-2 items-center">
                <div class="text-primary h-[16px] w-[16px] text-[16px] leading-[16px]">
                  <mat-icon [inline]="true">check_circle</mat-icon>
                </div>
                <span class="text-sm font-normal">Order In Progress</span>
              </div>
            }
          </div>
          <div class="w-full flex flex-col gap-4">
            <div class="h-2 rounded-2xl w-full bg-[#F8F7F8]"></div>
            @if (isMdDevice()) {
              <div class="ps-4 flex gap-2 items-center">
                <div class="h-[16px] w-[16px] rounded-full border border-[#F0EEF0]"></div>
                <span class="text-sm font-normal">Order Delivers</span>
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

  constructor(bpo: BreakpointObserver) {
    bpo
      .observe(Breakpoints.md)
      .pipe(takeUntilDestroyed())
      .subscribe((result) => this.isMdDevice.set(result.matches));
  }
}
