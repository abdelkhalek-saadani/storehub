import { Component, computed, inject, input } from '@angular/core';
import { MatIcon } from '@angular/material/icon';
import { MatIconButton } from '@angular/material/button';
import { Product } from '../../models/Product';
import { ProductStore } from '../../product-store';

@Component({
  selector: 'app-toggle-wishlist-button',
  imports: [MatIcon, MatIconButton],
  template: `
    <button
      class=" w-10 h-10 rounded-full !bg-white border-0 shadow-md flex items-center justify-center cursor-pointer transition-all duration-200 hover:scale-110 hover:shadow-lg"
      [class]="isInWishlist() ? '!text-red-500' : '!text-gray-400'"
      matIconButton
      (click)="toggleWishlist(product()!)"
    >
      <mat-icon>{{ isInWishlist() ? 'favorite' : 'favorite_border' }}</mat-icon>
    </button>
  `,
  styles: ``,
})
export class ToggleWishlistButton {
  store = inject(ProductStore);

  product = input<Product>();

  isInWishlist = computed(() => this.store.wishlist().find((p) => p.id === this.product()!.id));

  toggleWishlist(product: Product) {
    if (this.isInWishlist()) {
      this.store.removeFromWishlist(product);
    } else {
      this.store.addToWishlist(product);
    }
  }
}
