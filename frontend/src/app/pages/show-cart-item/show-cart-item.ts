import {Component, computed, inject, input, Input} from '@angular/core';
import {QtySelector, UpdateNature} from '../../components/qty-selector/qty-selector';
import {CartItem} from '../../models/CartItem';
import {MatIconButton} from '@angular/material/button';
import {MatIcon} from '@angular/material/icon';
import {DecimalPipe} from '@angular/common';
import {ProductStore} from '../../product-store';

@Component({
  selector: 'app-show-cart-item',
  imports: [
    QtySelector,
    MatIconButton,
    MatIcon,
    DecimalPipe
  ],
  template: `
    <div class="grid grid-cols-3 grid-cols-[3fr_1fr_1fr]">
      <div class="flex items-center gap-4">
        <div class="w-24 h-24 rounded-lg bg-gray-100 overflow-hidden flex items-center justify-center">
          <img width="96" height="96" class="w-full h-full object-cover"
               [src]="cartItem().product.imageUrl"
               [alt]="cartItem().product.name"></div>
        <div>
          <div class="text-gray-900 text-lg font-semibold">{{ cartItem().product.name }}</div>
          <div class="text-gray-600 text-lg">\${{ cartItem().product.price | number : '1.2-2' }}</div>
        </div>
      </div>
      <app-qty-selector [quantity]="cartItem().qty" (qtyUpdated)="updateQuantity($event)"/>
      <div class="flex flex-col items-end">
        <div class="text-right font-semibold text-lg"> \${{ subtotal() | number : '1.2-2' }}</div>
        <div class="flex -me-3">
          <button matIconButton>
            <mat-icon
              (click)="store.moveItemToWishlist(cartItem())"
            >favorite_border
            </mat-icon>
          </button>
          <button matIconButton
                  (click)="store.removeFromCart(cartItem())"
                  class="danger">
            <mat-icon>
              delete
            </mat-icon>
          </button>
        </div>
      </div>
    </div>
  `,
  styles: ``,
})
export class ShowCartItem {
  cartItem = input.required<CartItem>();
  subtotal = computed(() => (this.cartItem().product.price * this.cartItem().qty));
  store = inject(ProductStore);

  updateQuantity(updateNature: UpdateNature) {
    if (updateNature == 'increment')
      this.store.incrementQty(this.cartItem())
    else
      this.store.decrementQty(this.cartItem())
  }
}
