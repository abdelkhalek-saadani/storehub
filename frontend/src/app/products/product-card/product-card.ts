import { Component, Input } from '@angular/core';
import { MatCard, MatCardContent, MatCardImage } from '@angular/material/card';
import { AddToCartButton } from '@components/atoms/add-to-cart-button/add-to-cart-button';
import { Product } from '../models/product';

@Component({
  selector: 'app-product-card',
  imports: [AddToCartButton, MatCard, MatCardContent, MatCardImage],
  host: { class: 'block min-w-40 md:min-w-60 max-w-40 md:max-w-60' },
  template: `
    <mat-card class="relative rounded-2xl overflow-hidden h-full">
      <app-add-to-cart-button class="!absolute top-3 right-3 z-10" [quantity]="0" />

      <img
        mat-card-image
        src="product-img-placeholder.jpg"
        [alt]="product.name"
        class="h-48 w-full object-cover rounded-2xl"
      />

      <mat-card-content>
        <div class="flex flex-col gap-1 pt-4">
          <span class="text-base font-medium text-[#0D0C0D] leading-tight">{{ product.name }}</span>
          <span class="text-sm font-regular text-gray-400">{{ product.description }}</span>

          @if (product.activeDiscount) {
            <div class="flex items-center gap-2">
              <span class="text-sm text-gray-400 line-through">{{ product.unitPrice }} DT</span>
              <span class="text-lg font-semibold text-primary">{{ product.finalPrice }} DT</span>
            </div>
          } @else {
            <span class="text-lg font-semibold text-primary">{{ product.unitPrice }} DT</span>
          }
        </div>
      </mat-card-content>
    </mat-card>
  `,
})
export class ProductCard {
  @Input({ required: true }) product!: Product;
}
