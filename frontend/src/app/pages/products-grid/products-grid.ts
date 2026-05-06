import {Component, inject, input} from '@angular/core';
import {ProductCard} from '../../components/product-card/product-card';
import {TitleCasePipe} from '@angular/common';

import {ProductStore} from '../../product-store';

import {ToggleWishlistButton} from '../../components/toggle-wishlist-button/toggle-wishlist-button';


@Component({
  selector: 'app-products-grid',
  imports: [

    ProductCard,
    TitleCasePipe,

    ToggleWishlistButton,
  ],
  template: `

        <div class="bg-grey-100 p-6 h-full">
          <h1 class="text-2xl font-bold text-gray-900 mb-1"> {{ category() | titlecase }}</h1>
          <p class="text-base text-gray-600 mb-6">
            @if (store.filteredProducts().length != 0) {
              {{ store.filteredProducts().length }} products found
            } @else {
              No product found for this category :/
            }
          </p>

          <div class="responsive-grid">
            @for (product of store.filteredProducts(); track product.id) {
              <app-product-card [product]="product">
                <app-toggle-wishlist-button
                  class="!absolute z-10 top-3 right-3"
                  [style.view-transition-name]="'wishlist-button-'+product.id"
                  [product]="product"/>
              </app-product-card>
            }
          </div>
        </div>

  `,
  styles: ``,
})
export default class ProductsGrid {
  store = inject(ProductStore);
  category = input<string>('all');

  constructor() {
    this.store.setCategory(this.category);
    this.store.setProductListSeoTags(this.category);
  }


}
