import { Component, computed, inject, Input } from '@angular/core';
import { MatCard, MatCardContent, MatCardImage } from '@angular/material/card';
import { AddToCartButton } from '../add-to-cart-button/add-to-cart-button';
import { Product } from '../models/product';
import { CartStore } from '../../cart/cart-store';

@Component({
  selector: 'app-product-card',
  imports: [AddToCartButton, MatCard, MatCardContent, MatCardImage],
  host: { class: 'block min-w-40 md:min-w-60 max-w-40 md:max-w-60' },
  template: `
    <mat-card class="relative rounded-2xl overflow-hidden h-full">
      <app-add-to-cart-button
        class="!absolute top-3 right-3 z-10"
        [quantity]="quantity()"
        (qtyUpdated)="onQuantityUpdate($event)"
      />

      <img
        mat-card-image
        [src]="product.imageUrl"
        [alt]="product.name"
        class="h-48 w-full object-cover rounded-2xl"
      />

      <mat-card-content>
        <div class="flex flex-col gap-1 pt-4">
          <span class="text-base font-medium text-[#0D0C0D] leading-tight">{{ product.name }}</span>
          <span class="text-sm font-regular text-gray-400">{{ product.description }}</span>

          <span class="text-lg font-semibold text-primary">{{ product.unitPrice }} DT</span>
        </div>
      </mat-card-content>
    </mat-card>
  `,
})
export class ProductCard {
  @Input({ required: true }) product!: Product;
  cartStore = inject(CartStore);

  quantity = computed(() => {
    const i = this.cartStore.items().find((item) => item.productId === this.product.id);
    return i ? i.quantity : 0;
  });

  onQuantityUpdate(event: UpdateNature) {
    event === 'increment'
      ? this.cartStore.upsertItems([{ productId: this.product.id, quantity: this.quantity() + 1 }])
      : this.cartStore.upsertItems([{ productId: this.product.id, quantity: this.quantity() - 1 }]);
  }
}

export type UpdateNature = 'increment' | 'decrement';
