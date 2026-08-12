import { Component, computed, inject, input, OnInit, Signal, signal } from '@angular/core';
import { MatExpansionPanel, MatExpansionPanelHeader } from '@angular/material/expansion';
import { DecimalPipe, TitleCasePipe } from '@angular/common';
import { BreakpointObserver } from '@angular/cdk/layout';
import { Breakpoints } from '@core/constants/breakpoints';
import { rxResource, takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { CatalogApi } from '@shared/service/catalog-api';
import { OrderApi, OrderItemResponse } from '@shared/service/order-api';
import { UUID } from 'node:crypto';
import { CartItemResponse } from '../../../cart/model/cart-response';
import { number } from 'zod';
import { CartStore } from '../../../cart/cart-store';

@Component({
  selector: 'app-review-order',
  imports: [MatExpansionPanel, MatExpansionPanelHeader, TitleCasePipe, DecimalPipe],
  template: `
    <div class="flex flex-col p-6 gap-6 border border-[#F0EEF0] rounded-xl">
      <div class="font-semibold text-lg">Review Order</div>

      <mat-expansion-panel
        [expanded]="false"
        (opened)="isOpen.set(true)"
        (closed)="isOpen.set(false)"
        class="p-3 bg-black"
      >
        <mat-expansion-panel-header>
          @if (!isOpen()) {
            <div class="pe-4 flex flex-row gap-2">
              @for (item of headerItems(); track item.itemId) {
                <div class="w-16 rounded-2xl h-16 aspect-square overflow-hidden">
                  <img
                    [src]="
                      item.productImageUrl ??
                      'https://images.unsplash.com/photo-1603751915495-a5a3ec39c7f5'
                    "
                    [alt]="item.productName + ' image'"
                  />
                </div>
              }
              @if (headerItems().length != orderedItems().length) {
                <div class="w-16 rounded-2xl h-16 aspect-square flex items-center justify-center">
                  + {{ items().length - headerItems().length }}
                </div>
              }
            </div>
          }
        </mat-expansion-panel-header>
        <div class="flex flex-col gap-4">
          @for (item of orderedItems(); track item.itemId) {
            <div
              class="p-3 flex flex-row justify-between items-center bg-white rounded-2xl border-b border-b-[#F0EEF0]"
            >
              <div class="flex flex-row gap-4">
                <div class="w-16 rounded-2xl h-16 aspect-square overflow-hidden">
                  <img
                    [src]="
                      item.productImageUrl ??
                      'https://images.unsplash.com/photo-1603751915495-a5a3ec39c7f5'
                    "
                    [alt]="item.productImageUrl + 'image'"
                  />
                </div>
                <div class="flex flex-col gap-2">
                  <div class="text-base font-medium">{{ item.productName | titlecase }}</div>
                  <div class="flex flex-row gap-2 items-center">
                    <div class="font-medium text-base">
                      {{ item.finalLineTotal / item.quantity | number: '1.2-2' }}DT
                    </div>
                    @if (item.discountAmount != 0) {
                      <div class="line-through text-xs font-regular text-black/60">
                        {{ item.originalLineTotal / item.quantity | number: '1.2-2' }}DT
                      </div>
                    }
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
export class ReviewOrder implements OnInit {
  isOpen = signal(false);

  items = input.required<CartItemResponse[] | OrderItemResponse[]>();

  isXSMobile = signal(false);

  constructor(bpo: BreakpointObserver) {
    bpo
      .observe(Breakpoints.sm)
      .pipe(takeUntilDestroyed())
      .subscribe((result) => this.isXSMobile.set(result.matches));
  }

  orderedItems = computed((): (CartItemResponse | OrderItemResponse)[] => {
    return [...this.items()].sort((a, b) => a.productId.localeCompare(b.productId));
  });

  headerItems = computed(() => {
    if (this.isXSMobile()) {
      return this.items().slice(0, 2);
    }
    return this.items().slice(0, 5);
  });

  ngOnInit(): void {}
}
