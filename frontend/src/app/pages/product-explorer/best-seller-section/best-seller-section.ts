import { Component, ElementRef, inject, signal, ViewChild } from '@angular/core';
import { ProductCard } from '../../../components/molecules/product-card/product-card';
import { MatButton, MatIconButton } from '@angular/material/button';
import { MatIcon } from '@angular/material/icon';
import { BreakpointObserver, Breakpoints } from '@angular/cdk/layout';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { NgClass } from '@angular/common';
import { ScrollArrows } from '../scroll-arrows/scroll-arrows';

@Component({
  selector: 'app-best-seller-section',
  imports: [ProductCard, MatButton, MatIcon, NgClass, ScrollArrows],
  template: `
    <div
      class="flex flex-col gap-4 md:gap-6 md:p-6 md:rounded-lg md:border md:border-[#F0EEF0] md:bg-white"
    >
      <div class="flex items-center justify-between">
        <span class="font-semibold text-black text-lg md:text-2xl ">Best Seller</span>
        <div class="flex items-center gap-6">
          <button matButton="filled" [ngClass]="isMobile() ? 'btn-sm' : 'btn-md'">
            View All
            <mat-icon iconPositionEnd>arrow_forward</mat-icon>
          </button>
          @if (!isMobile()) {
            <app-scroll-arrows (arrowClicked)="scroll($event)" />
          }
        </div>
      </div>
      <div
        #scrollContainer
        class="flex gap-4 pb-2 overflow-x-auto snap-x snap-mandatory scroll-smooth
               [-ms-overflow-style:none] [scrollbar-width:none] [&::-webkit-scrollbar]:hidden"
      >
        <app-product-card class="snap-start " />
        <app-product-card class="snap-start " />
        <app-product-card class="snap-start " />
        <app-product-card class="snap-start " />
      </div>
    </div>
  `,
  styles: ``,
})
export class BestSellerSection {
  private breakpointObserver = inject(BreakpointObserver);
  isMobile = signal(false);
  @ViewChild('scrollContainer') scrollContainer!: ElementRef;

  constructor() {
    this.breakpointObserver
      .observe('(max-width: 768px)')
      .pipe(takeUntilDestroyed())
      .subscribe((result) => this.isMobile.set(result.matches));
  }
  scroll(direction: 'left' | 'right') {
    this.scrollContainer.nativeElement.scrollBy({
      left: direction === 'right' ? 300 : -300,
      behavior: 'smooth',
    });
  }
}
