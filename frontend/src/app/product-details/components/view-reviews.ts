import { Component, input, signal } from '@angular/core';
import { StarRating } from './star-rating';
import { MatIcon } from '@angular/material/icon';
import { ViewReviewItem } from './view-review-item';
import { RatingSummary } from './rating-summary';
import { Review } from '../../models/Review';
import { ViewPanel } from '../directives/view-panel';
import { Product } from '../../models/Product';
import { MatButton } from '@angular/material/button';
import { WriteReview } from './write-review';

@Component({
  selector: 'app-view-reviews',
  imports: [RatingSummary, ViewReviewItem, ViewPanel, MatButton, WriteReview],
  template: `
    <div appViewPanel="" class=" rounded-xl p-6 bg-white">
      <div class="flex justify-between items-center mb-4">
        <h2 class="text-xl font-semibold">Customer Reviews</h2>
        @if (!showWriteReview()) {
          <button matButton="filled" (click)="openWR()">Write a Review</button>
        } @else {
          <button matButton="filled" (click)="closeWR()">Hide</button>
        }
      </div>

      @if (showWriteReview()) {
        <app-write-review
          [productId]="productId()!"
          class="block mb-6"
          (canceled)="closeWR()"
          (reviewAdded)="closeWR()"
        />
      }

      <app-rating-summary [reviews]="reviews()" />
      <div class="flex flex-col gap-6">
        @for (review of reviews(); track $index) {
          <app-view-review-item [review]="review" />
        }
      </div>
    </div>
  `,
  styles: ``,
})
export class ViewReviews {
  reviews = input.required<Review[]>();

  productId = input<string>();

  showWriteReview = signal(true);

  openWR() {
    this.showWriteReview.set(true);
  }
  closeWR() {
    this.showWriteReview.set(false);
  }
}
