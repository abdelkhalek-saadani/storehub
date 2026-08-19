import { Component } from '@angular/core';
import { MatButton, MatIconButton } from '@angular/material/button';
import { MatIcon } from '@angular/material/icon';
import { Gallery } from './components/gallery';
import { ViewReviews } from './components/view-reviews';
import { Review } from '../models/Review';

@Component({
  selector: 'app-product-details',
  imports: [MatIconButton, MatIcon, Gallery, MatButton, ViewReviews],
  host: {
    class: 'min-h-screen flex flex-col px-4 bg-[#F8F8F8] pt-2',
  },
  template: `
    <div class="flex flex-col md:bg-white md:p-4 md:rounded-2xl md:mt-16 md:mx-20 md:gap-5">
      <div class="flex justify-between items-center">
        <button matIconButton class="back-button">
          <mat-icon>arrow_back</mat-icon>
        </button>
        <app-toggle-wishlist-button />
      </div>
      <div class="flex flex-col md:flex-row md:items-center md:gap-20">
        <div class="p-4">
          <app-gallery />
        </div>
        <div class="flex flex-col gap-4 md:max-w-[520px]">
          <span class="mt-2 font-semibold text-[28px] text-[#0D0C0D] md:text-[36px]">
            Chosen Best Food
          </span>
          <div class="text-xl font-bold text-[#292929]">Description</div>
          <p class="text-[#666666] font-base font-normal">
            Boba etiam ut bulla tea est potus dilectus singulari compositione saporum et textuum,
            quae in Taiwan annis 1980 orta sunt. Boba refert ad pilas masticas tapiocas in fundo
            potus inventas, quae typice lacte tea nigro sapiuntur. Boba phaenomenon.
          </p>
          <div class="flex gap-2 items-center">
            <span class="text-[#0D0C0D] font-semibold text-[28px]">99.99DT</span>
            <span class="text-[#807681] font-normal text-lg line-through">99.99DT</span>
          </div>
          <button matButton="filled" class="btn-pill">
            Add To Cart
            <mat-icon>add_shopping_cart</mat-icon>
          </button>
        </div>
      </div>
    </div>
    <div class="mt-5 md:rounded-2xl md:mx-20">
      <app-view-reviews [reviews]="reviews" />
    </div>
  `,
  styles: ``,
})
export default class ProductDetails {
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
