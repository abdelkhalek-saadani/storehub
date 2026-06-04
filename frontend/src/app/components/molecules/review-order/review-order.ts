import { Component, computed, signal } from '@angular/core';
import { MatExpansionPanel, MatExpansionPanelHeader } from '@angular/material/expansion';
import { TitleCasePipe } from '@angular/common';
import { BreakpointObserver } from '@angular/cdk/layout';
import { Breakpoints } from '@core/constants/breakpoints';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';

@Component({
  selector: 'app-review-order',
  imports: [MatExpansionPanel, MatExpansionPanelHeader, TitleCasePipe],
  template: `
    <div class="flex flex-col p-6 gap-6 border border-[#F0EEF0] rounded-xl">
      <div class="font-semibold text-lg">Review Order</div>

      <mat-expansion-panel
        [expanded]="true"
        (opened)="isOpen.set(true)"
        (closed)="isOpen.set(false)"
        class="p-3 bg-black"
      >
        <mat-expansion-panel-header>
          @if (!isOpen()) {
            <div class="pe-4 flex flex-row gap-2">
              @for (item of headerItems(); track item.id) {
                <div class="w-16 rounded-2xl h-16 aspect-square overflow-hidden">
                  <img [src]="item.imgUrl" [alt]="item.name + ' image'" />
                </div>
              }
              @if (headerItems().length != items().length) {
                <div class="w-16 rounded-2xl h-16 aspect-square flex items-center justify-center">
                  + {{ items().length - headerItems().length }}
                </div>
              }
            </div>
          }
        </mat-expansion-panel-header>
        <div class="flex flex-col gap-4">
          @for (item of items(); track item.id) {
            <div
              class="p-3 flex flex-row justify-between items-center bg-white rounded-2xl border-b border-b-[#F0EEF0]"
            >
              <div class="flex flex-row gap-4">
                <div class="w-16 rounded-2xl h-16 aspect-square overflow-hidden">
                  <img [src]="item.imgUrl" [alt]="item.name + 'image'" />
                </div>
                <div class="flex flex-col gap-2">
                  <div class="text-base font-medium">{{ item.name | titlecase }}</div>
                  <div class="flex flex-row gap-2 items-center">
                    <div class="font-medium text-base">{{ item.price }}DT</div>
                    <div class="line-through text-xs font-regular text-black/60">
                      {{ item.originalPrice }}DT
                    </div>
                  </div>
                </div>
              </div>
              <div class="text-sm font-medium text-grey-500">x{{ item.quantity }}</div>
            </div>
          }
        </div>
      </mat-expansion-panel>
    </div>
  `,
  styles: ``,
})
export class ReviewOrder {
  isOpen = signal(false);

  isXSMobile = signal(false);

  constructor(bpo: BreakpointObserver) {
    bpo
      .observe(Breakpoints.sm)
      .pipe(takeUntilDestroyed())
      .subscribe((result) => this.isXSMobile.set(result.matches));
  }

  items = signal([
    {
      id: 1,
      name: 'Natural Milk',
      imgUrl: 'product-img-placeholder.jpg',
      quantity: 1,
      originalPrice: 6.0,
      price: 5.0,
    },
    {
      id: 2,
      name: 'Natural Milk',
      imgUrl: 'product-img-placeholder.jpg',
      quantity: 1,
      originalPrice: 6.0,
      price: 5.0,
    },
    {
      id: 3,
      name: 'Natural Milk',
      imgUrl: 'product-img-placeholder.jpg',
      quantity: 1,
      originalPrice: 6.0,
      price: 5.0,
    },
    {
      id: 4,
      name: 'Natural Milk',
      imgUrl: 'product-img-placeholder.jpg',
      quantity: 1,
      originalPrice: 6.0,
      price: 5.0,
    },
    {
      id: 5,
      name: 'Natural Milk',
      imgUrl: 'product-img-placeholder.jpg',
      quantity: 1,
      originalPrice: 6.0,
      price: 5.0,
    },
    {
      id: 6,
      name: 'Natural Milk',
      imgUrl: 'product-img-placeholder.jpg',
      quantity: 1,
      originalPrice: 6.0,
      price: 5.0,
    },
    {
      id: 7,
      name: 'Natural Milk',
      imgUrl: 'product-img-placeholder.jpg',
      quantity: 1,
      originalPrice: 6.0,
      price: 5.0,
    },
  ]);

  headerItems = computed(() => {
    if (this.isXSMobile()) {
      return this.items().slice(0, 2);
    }
    return this.items().slice(0, 5);
  });
}
