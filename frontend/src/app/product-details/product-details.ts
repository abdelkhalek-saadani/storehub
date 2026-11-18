import { Component, computed, inject, input } from '@angular/core';
import { MatButton, MatIconButton } from '@angular/material/button';
import { MatIcon } from '@angular/material/icon';
import { Gallery } from './components/gallery';
import { ViewReviews } from './components/view-reviews';
import { Review } from '../models/Review';
import { ToggleWishlistButton } from './components/toggle-wishlist-button';
import { Location, TitleCasePipe } from '@angular/common';
import { rxResource } from '@angular/core/rxjs-interop';
import { Product } from '../products/models/product';
import { CatalogApi } from '@shared/service/catalog-api';
import { MatProgressSpinner } from '@angular/material/progress-spinner';
import { tap } from 'rxjs';
import { AddToCartButton } from './components/add-to-cart-button';
import { CartStore } from '../cart/cart-store';
import { UpdateNature } from '../products/product-card/product-card';

@Component({
  selector: 'app-product-details',
  imports: [
    MatIconButton,
    MatIcon,
    Gallery,
    MatButton,
    ViewReviews,
    ToggleWishlistButton,
    TitleCasePipe,
    MatProgressSpinner,
    AddToCartButton,
  ],
  host: {
    class: 'min-h-screen flex flex-col px-4 bg-[#F8F8F8] pt-2',
  },
  template: `
    @if (product.value(); as p) {
      <div class="flex flex-col md:bg-white md:p-4 md:rounded-2xl md:mt-16 md:mx-20 md:gap-5">
        <div class="flex justify-between items-center">
          <button matIconButton class="back-button" (click)="goBack()" )>
            <mat-icon>arrow_back</mat-icon>
          </button>
          <app-toggle-wishlist-button />
        </div>

        <div class="flex flex-col md:flex-row md:items-center md:gap-20">
          <div class="p-4">
            <app-gallery [imgUrl]="p.imageUrl" />
          </div>
          <div class="flex flex-col gap-4 md:max-w-[520px]">
            <span class="mt-2 font-semibold text-[28px] text-[#0D0C0D] md:text-[36px]">
              {{ p.name | titlecase }}
            </span>
            <div class="text-xl font-bold text-[#292929]">Description</div>
            <p class="text-[#666666] font-base font-normal">
              @if (p.description != null) {
                {{ p.description }}
              } @else {
                Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor
                incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud
                exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute
                irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla
                pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia
                deserunt mollit anim id est laborum
              }
            </p>
            <div class="flex gap-2 items-center">
              <span class="text-[#0D0C0D] font-semibold text-[28px]">{{ p.unitPrice }}</span>
            </div>

            <div class="flex items-center h-[48px]">
              <app-add-to-cart-button
                [quantity]="quantity()"
                (qtyUpdated)="onQuantityUpdate($event)"
              />
            </div>
          </div>
        </div>
      </div>
      <div class="mt-5 md:rounded-2xl md:mx-20">
        <app-view-reviews [reviews]="reviews" />
      </div>
    } @else if (product.isLoading()) {
      <mat-spinner />
    } @else if (product.error()) {
      <span>Product with id {{ productId() }} not found</span>
    }
  `,
  styles: ``,
})
export default class ProductDetails {
  private location = inject(Location);
  productId = input.required<string>();

  cartStore = inject(CartStore);

  quantity = computed(() => {
    const p = this.product.value() as Product;
    const i = this.cartStore.items().find((item) => item.productId === p.id);
    return i ? i.quantity : 0;
  });

  onQuantityUpdate(event: UpdateNature) {
    const p = this.product.value() as Product;
    if (event === 'increment')
      this.cartStore.upsertItems([{ productId: p.id, quantity: this.quantity() + 1 }]);
    else this.cartStore.upsertItems([{ productId: p.id, quantity: this.quantity() - 1 }]);
  }

  goBack(): void {
    this.location.back();
  }

  private catalogApi = inject(CatalogApi);
  product = rxResource<Product, unknown>({
    stream: () => this.catalogApi.getProduct(this.productId()).pipe(tap((p) => console.log(p))),
  });

  reviews: Review[] = [
    {
      name: 'Sarah Mitchell',
      avatar: 'https://i.pravatar.cc/150?img=1',
      rating: 5,
      date: '2025-03-12',
      title: 'Absolutely love this product!',
      comment: 'The quality exceeded my expectations. Will definitely be ordering again.',
    },
    {
      name: 'James Okafor',
      avatar: 'https://i.pravatar.cc/150?img=3',
      rating: 4,
      date: '2025-04-01',
      title: 'Great value for the price',
      comment:
        'Solid product overall. Packaging could be improved but the product itself is excellent.',
    },
    {
      name: 'Lena Bauer',
      avatar: 'https://i.pravatar.cc/150?img=5',
      rating: 3,
      date: '2025-04-18',
      title: 'Decent but not perfect',
      comment: 'Does what it promises but I expected a bit more based on the description.',
    },
    {
      name: 'Youssef Mansour',
      avatar: 'https://i.pravatar.cc/150?img=7',
      rating: 5,
      date: '2025-05-03',
      title: 'Best purchase this month',
      comment:
        'Fast delivery, great quality. Highly recommend to anyone looking for a reliable option.',
    },
    {
      name: 'Chloe Nguyen',
      avatar: 'https://i.pravatar.cc/150?img=9',
      rating: 4,
      date: '2025-05-20',
      title: 'Very happy with my order',
      comment: 'Fresh, well packaged and exactly as described. Will be a returning customer.',
    },
  ];
}
