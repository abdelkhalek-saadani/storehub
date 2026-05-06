import {Component, computed, inject, input} from '@angular/core';
import {BackButton} from '../../components/back-button/back-button';
import {ProductStore} from '../../product-store';
import {ProductInfo} from '../../components/product-info/product-info';
import {ViewReviews} from '../../components/view-reviews/view-reviews';
import {Product} from '../../models/Product';

@Component({
  selector: 'app-product-details',
  imports: [
    BackButton,
    ProductInfo,
    ViewReviews
  ],
  template: `
    <div class="mx-auto max-w-[1200px] py-6">
      <app-back-button
        class="block mb-6"
        [navigateTo]="'/products/' + store.category()"
        label="Back to Products"
      />
      <div class="flex gap-8 mb-8">
        <img class="w-[500px] h-[550px] object-cover rounded-lg"
             [style.view-transition-name]="'product-image-2-' + product()!.id"
             [src]="product()!.imageUrl"
             [alt]="product()!.name">
        <div class="flex-1">
          <app-product-info [product]="product()!"/>
        </div>
      </div>
      <app-view-reviews [productId]="productId()" [reviews]="reviews()"/>
    </div>
  `,
  styles: ``,
})
export default class ProductDetails {
  productId = input.required<string>();
  store = inject(ProductStore);
  product = computed(
    () => this.store.products().find((p) => p.id == this.productId()));

  reviews = computed(
    () => this.product()!.reviews.toSorted((a, b) => Date.parse(b.date) - Date.parse(a.date))
  )

  constructor() {
    this.store.setProductSeoTags(this.product);
  }


}
