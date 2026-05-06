import {Component, computed, inject, input, output} from '@angular/core';
import {MatButton, MatIconButton} from '@angular/material/button';
import {MatIcon} from '@angular/material/icon';
import {Product} from '../../models/Product';
import {ProductStore} from '../../product-store';
import {RouterLink} from '@angular/router';
import {StarRating} from '../star-rating/star-rating';

@Component({
  selector: 'app-product-card',
  imports: [
    MatButton,
    MatIcon,
    RouterLink,
    StarRating
  ],
  template: `
    <div
      class="relative flex flex-col bg-white rounded-xl shadow-lg overflow-hidden h-full transition-all duration-200 ease-out hover:-translate-y-1 hover:shadow-xl">
      <img [src]="product().imageUrl"
           class="cursor-pointer w-full h-[300px] object-cover rounded-t-xl"
           alt="product image"
           [style.view-transition-name]="'product-image-2-' + product()!.id"
           [routerLink]="'/product/' + product().id "/>
      <ng-content/>
      <div class="p-5 flex flex-col flex-1 justify-between gap-4">
        <div class="flex flex-col gap-1">
          <h3
            class="h-[3rem] border border-red-500 text-lg font-semibold text-gray-900 leading-tight ">{{ product().name }}</h3>
          <p class="border border-red-500 text-sm text-gray-600 leading-relaxed"> {{ product().description }}</p>
        </div>
        <div class="flex flex-col gap-1">

          <div class="border border-red-500  flex items-center">
            <app-star-rating [rate]="product().rating">
              ({{ product().reviewCount }})
            </app-star-rating>

          </div>
          <div class="border border-red-500 text-sm font-medium ">
            @if (product().inStock) {
              <p class="green">In Stock</p>
            } @else {
              <p class="red">Out of Stock</p>
            }
          </div>
          <div class="border border-red-500  flex items-center justify-between">
            <span class="text-2xl font-bold text-gray-900">\$ {{ product().price }}</span>
            <button matButton="filled" class="flex items-center gap-2" (click)="addToCart(product())">
              <mat-icon>shopping_cart</mat-icon>
              <span>Add to Cart</span>
            </button>
          </div>
        </div>
      </div>
    </div>
  `,
  styles: ``,
})
export class ProductCard {

  product = input.required<Product>();
  protected readonly Array = Array;
  store = inject(ProductStore);

  addToCart(product: Product) {
    this.store.addToCart(product);
  }


}
