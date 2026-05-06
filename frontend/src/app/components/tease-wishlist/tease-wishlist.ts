import {Component, inject} from '@angular/core';
import {MatIcon} from '@angular/material/icon';
import {ProductStore} from '../../product-store';
import {ViewPanel} from '../../directives/view-panel';
import {MatButton} from '@angular/material/button';
import {RouterLink} from '@angular/router';

@Component({
  selector: 'app-tease-wishlist',
  imports: [
    MatIcon,
    ViewPanel,
    MatButton,
    RouterLink
  ],
  template: `
    <div appViewPanel class="border border-gray-200 rounded-xl p-6 bg-white flex items-center justify-between">
      <div class="flex items-center gap-3">
        <mat-icon
          class="!text-red-500"
        >favorite_border
        </mat-icon>
        <div><h2 class="text-xl font-bold">Wishlist ({{ store.wishlistCounter() }} items)</h2>
          <p class="text-gray-500 text-sm"> You have {{ store.wishlistCounter() }} item saved for later </p></div>
      </div>
      <div class="flex items-center gap-3">
        <button matButton routerLink="/wishlist">
          View All
        </button>
        <button
          matButton="filled"
          class=" flex items-center gap-2"
          (click)="store.moveWishlistToCart()"
        >
          <mat-icon>
            shopping_cart
          </mat-icon>
          Add All to Cart
        </button>
      </div>
    </div>
  `,
  styles: ``,
})
export class TeaseWishlist {
  store = inject(ProductStore);
}
