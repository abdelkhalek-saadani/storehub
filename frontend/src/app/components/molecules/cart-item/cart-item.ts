import { Component, computed, inject, input } from '@angular/core';
import { MatButton, MatIconButton } from '@angular/material/button';
import { MatIcon } from '@angular/material/icon';
import { CartItemResponse } from '../../../cart/model/cart-response';
import { CartStore } from '../../../cart/cart-store';
import { DecimalPipe } from '@angular/common';

@Component({
  selector: 'app-cart-item',
  imports: [MatButton, MatIcon, MatIconButton, DecimalPipe],
  template: `
    <div class="flex flex-col pb-4 gap-3">
      <div class="flex flex-row gap-5 items-center">
        <div class="rounded-2xl h-26 aspect-square overflow-hidden">
          <img src="product-img-placeholder.jpg" alt="Natural Milk" />
        </div>
        <div class="flex flex-col gap-3">
          <div class="text-base font-medium">{{ item().productName }}</div>
          <div class="flex flex-row gap-3">
            <div class="text-xl font-semibold text-primary">
              {{ item().unitPrice | number: '1.2-2' }}
            </div>
          </div>
          <span class="text-2xl text-red-300">{{ item().appliedOfferLabel }}</span>
        </div>
      </div>
      <div class="flex flex-row gap-6 justify-between items-center">
        <div class="flex flex-row gap-3 items-center">
          <div class="font-semibold text-base">{{ item().finalLineTotal | number: '1.2-2' }}</div>
          @if (originalLineTotal() != finalLineTotal()) {
            <div class="text-xl font-regular text-[#A2A2A2] line-through">
              {{ item().originalLineTotal | number: '1.2-2' }}
            </div>
          }
        </div>
        <div class="flex flex-row gap-4 items-center">
          <button matButton="text" class="sidecart-button" (click)="onRemoveItem(item().productId)">
            Remove
          </button>
          <div class="flex flex-row items-center bg-[#F6FAFF]">
            <button
              matIconButton
              class="sidecart-button"
              (click)="onDecrementQty(item().productId)"
            >
              <mat-icon>remove</mat-icon>
            </button>
            <div class="text-sm font-medium px-1">{{ item().quantity }}</div>
            <button
              matIconButton
              class="sidecart-button"
              (click)="onIncrementQty(item().productId)"
              )
            >
              <mat-icon>add</mat-icon>
            </button>
          </div>
        </div>
      </div>
    </div>
  `,
  styles: ``,
})
export class CartItem {
  item = input.required<CartItemResponse>();

  store = inject(CartStore);

  originalLineTotal = computed(() => this.item().originalLineTotal.toPrecision(2));
  finalLineTotal = computed(() => this.item().finalLineTotal.toPrecision(2));

  onIncrementQty(productId: string): void {
    const current = this.store.items().find((i) => i.productId === productId);
    const newQty = (current?.quantity ?? 0) + 1;
    this.store.upsertItems([{ productId, quantity: newQty }]);
  }

  onDecrementQty(productId: string): void {
    this.onQuantityChange(productId, this.item().quantity - 1);
  }

  onRemoveItem(productId: string): void {
    this.store.upsertItems([{ productId, quantity: 0 }]);
  }

  onQuantityChange(productId: string, quantity: number): void {
    this.store.upsertItems([{ productId, quantity }]);
  }
}
