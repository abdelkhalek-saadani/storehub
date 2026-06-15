import { Component, ElementRef, inject, signal, ViewChild } from '@angular/core';
import { ProductCard } from '../../../components/molecules/product-card/product-card';
import { MatButton } from '@angular/material/button';
import { MatIcon } from '@angular/material/icon';
import { BreakpointObserver } from '@angular/cdk/layout';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { NgClass } from '@angular/common';
import { ScrollArrows } from '../scroll-arrows/scroll-arrows';

@Component({
  selector: 'app-products-section',
  imports: [ProductCard, MatButton, MatIcon, NgClass, ScrollArrows],
  template: `
    <div
      class="flex flex-col gap-4 pt-6 md:gap-6 md:p-6 md:rounded-lg md:border md:border-[#F0EEF0] md:bg-white md:mt-8"
    >
      <div class="flex items-center justify-between">
        <span class="font-semibold text-black text-lg md:text-2xl ">Products</span>
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
        class="grid grid-rows-2 grid-flow-col gap-4 overflow-x-auto snap-x snap-mandatory scroll-smooth px-4 pb-4 [scrollbar-width:none] [-ms-overflow-style:none] [&::-webkit-scrollbar]:hidden"
      >
        @for (i of [].constructor(10); track $index) {
          <app-product-card class="snap-start shrink-0" />
        }
      </div>
    </div>
  `,
  styles: ``,
})
export class ProductsSection {
  private breakpointObserver = inject(BreakpointObserver);
  isMobile = signal(false);
  @ViewChild('scrollContainer') scrollContainer!: ElementRef;

  scroll(direction: 'left' | 'right') {
    this.scrollContainer.nativeElement.scrollBy({
      left: direction === 'right' ? 300 : -300,
      behavior: 'smooth',
    });
  }

  constructor() {
    this.breakpointObserver
      .observe('(max-width: 768px)')
      .pipe(takeUntilDestroyed())
      .subscribe((result) => this.isMobile.set(result.matches));
  }
}
