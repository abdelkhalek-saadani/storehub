import { Component, signal } from '@angular/core';
import { MatIcon } from '@angular/material/icon';
import { MatButton, MatIconButton } from '@angular/material/button';
import { BreakpointObserver, Breakpoints } from '@angular/cdk/layout';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { Breakpoints as Bp } from '@core/constants/breakpoints';
import { NgClass } from '@angular/common';
import { ActionButtons } from './action-buttons/action-buttons';

@Component({
  selector: 'app-wishlist-product-card',
  imports: [ActionButtons],
  template: `
    <div class="py-4 flex gap-6 items-center justify-between">
      <div class="flex gap-4 items-center">
        <div class="rounded-2xl aspect-square overflow-hidden h-21 md:h-51">
          <img src="product-img-placeholder.jpg" alt="Natural Milk" />
        </div>
        <div class="flex flex-col gap-2">
          <span class="font-medium text-sm md:text-2xl text-black-900">Sweet Green Seedless</span>
          <span class="font-medium text-xs md:text-sm text-[#555454]">Description</span>
          <div class="flex gap-2">
            <span class="text-lg md:text-2xl font-semibold text-primary">99.99TND</span>
            <span class="text-lg md:text-2xl font-normal text-[#9C939D] line-through"
              >115.00TND</span
            >
          </div>
        </div>
      </div>
      @if (isMdDevice()) {
        <app-action-buttons type="desktop" />
      } @else {
        <app-action-buttons type="mobile" />
      }
    </div>
  `,
  styles: ``,
})
export class WishlistProductCard {
  isMdDevice = signal(false);
  constructor(bpo: BreakpointObserver) {
    bpo
      // .observe([Breakpoints.Handset, Breakpoints.TabletPortrait])
      .observe(Bp.md)
      .pipe(takeUntilDestroyed())
      .subscribe((result) => this.isMdDevice.set(result.matches));
  }
}
