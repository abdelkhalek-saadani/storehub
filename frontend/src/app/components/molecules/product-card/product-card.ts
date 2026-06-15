import { Component } from '@angular/core';
import { AddToCartButton } from '../../atoms/add-to-cart-button/add-to-cart-button';
import { MatCard, MatCardContent, MatCardImage } from '@angular/material/card';

@Component({
  selector: 'app-product-card',
  imports: [AddToCartButton, MatCard, MatCardContent, MatCardImage],
  host: {
    class: 'block min-w-40 md:min-w-60 max-w-40 md:max-w-60',
  },
  template: `
    <mat-card class="relative rounded-2xl overflow-hidden">
      <app-add-to-cart-button class="!absolute top-3 right-3 z-10" [quantity]="5" />

      <img
        mat-card-image
        src="product-img-placeholder.jpg"
        alt="Natural Milk"
        class="h-48 w-full object-cover rounded-2xl"
      />

      <mat-card-content>
        <div class="flex flex-col gap-1 pt-4">
          <span class="text-base font-medium text-[#0D0C0D] leading-tight"> Natural Milk </span>

          <span class="text-sm font-regular text-gray-400"> By bottle of 1L </span>

          <span class="text-lg font-semibold text-primary"> 1.5 DT </span>
        </div>
      </mat-card-content>
    </mat-card>
  `,
  styles: ``,
})
export class ProductCard {}
