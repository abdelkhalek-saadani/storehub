import {Component, inject, input, signal} from '@angular/core';
import {Product} from '../../models/Product';
import {StarRating} from '../star-rating/star-rating';
import {StockStatus} from '../stock-status/stock-status';
import {ProductStore} from '../../product-store';
import {QtySelector, UpdateNature} from '../qty-selector/qty-selector';
import {MatButton, MatIconButton} from '@angular/material/button';
import {ToggleWishlistButton} from '../toggle-wishlist-button/toggle-wishlist-button';
import {MatIcon} from '@angular/material/icon';
import {TitleCasePipe} from '@angular/common';

@Component({
  selector: 'app-product-info',
  imports: [
    StarRating,
    StockStatus,
    QtySelector,
    MatButton,
    ToggleWishlistButton,
    MatIconButton,
    MatIcon,
    TitleCasePipe
  ],
  template: `

    <div class="text-xs rounded-xl bg-gray-100 px-2 py-1 w-fit mb-2">
      {{ product()?.category | titlecase }}
    </div>
    <h1 class="text-2xl font-extrabold mb-3">
      {{ product()?.name }}
    </h1>
    <app-star-rating class="mb-3 block" [rate]="product()?.rating"/>
    <p class="text-3xl font-extrabold mb-4">
      \${{ product()?.price }}
    </p>
    <app-stock-status class="block mb-4"/>
    <p class="font-semibold mb-2">
      Description
    </p>
    <p class="text-gray-600 border-b border-gray-200 pb-4">
      {{ product()?.description }}
    </p>
    <div class="flex items-center gap-2 mb-3 pt-4">
      <span class="font-semibold">
        Quantity:
      </span>
      <app-qty-selector [quantity]="quantity()" (qtyUpdated)="updateQuantity($event)"/>
    </div>
    <div class="flex gap-4 mb border-b border-gray-200 pb-4">
      <button
        matButton="filled"
        class="w-2/3 flex items-center gap-2"
        (click)="store.addToCart(product()!,quantity())"
      >
        <mat-icon>
          shopping_cart
        </mat-icon>
        Add to Cart
      </button>
      <app-toggle-wishlist-button [product]="product()!"/>
      <button matIconButton>
        <mat-icon>
          share
        </mat-icon>
      </button>
    </div>
    <div class="pt-6 flex flex-col gap-2 text-gray-700 text-xs">
      <div class="flex items-center gap-3">
        <mat-icon>
          local_shipping
        </mat-icon>
        <span>Free shipping on orders over $50</span></div>
      <div class="flex items-center gap-3">
        <mat-icon>
          autorenew
        </mat-icon>
        <span>30-day return policy</span></div>
      <div class="flex items-center gap-3">
        <mat-icon>
          shield
        </mat-icon>
        <span>2-year warranty included</span></div>
    </div>

  `,
  styles: ``,
})
export class ProductInfo {
  store = inject(ProductStore);
  quantity = signal(1);
  product = input<Product>();
  category = this.store.category;

  ngOnInit(){
    const cartItem = this.store.cartItems().find((ci)=> ci.product.id==this.product()?.id);
    if (cartItem){
      this.quantity.set(cartItem.qty);
    }
  }

  updateQuantity(updateNature: UpdateNature) {
    if (updateNature == 'increment')
      this.quantity.set(this.quantity()+1);
    else
      this.quantity.set(this.quantity()-1);
  }

}
