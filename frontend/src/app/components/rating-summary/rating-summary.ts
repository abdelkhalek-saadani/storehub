import {Component, computed, input, Input} from '@angular/core';
import {MatIcon} from '@angular/material/icon';
import {StarRating} from '../star-rating/star-rating';
import {Review} from '../../models/Review';
import {DecimalPipe} from '@angular/common';

@Component({
  selector: 'app-rating-summary',
  imports: [
    StarRating,
    DecimalPipe
  ],
  template: `
    <div class="flex items-center gap-8 mb-6 p-4 bg-gray-50 rounded-lg">
      <div class="flex flex-col items-center w-1/2">
        <div class="text-4xl font-bold text-gray-900 mb-1">
          {{ ratingAvg() | number : '1.1-1' }}
        </div>
        <div class="flex items-center mb-2">
          <app-star-rating [rate]="Math.floor(ratingAvg())"/>
        </div>
        <div class="text-sm text-gray-500">
          Based on {{ reviewCount() }} reviews
        </div>
      </div>
      <div class="flex-1">

        @for (reviewBreakdown of reviewsBreakdown(); track $index) {
          <div class="flex items-center gap-2 mb-2">
            <span class="text-sm w-4">{{ reviewBreakdown.star }}★</span>
            <div class="flex-1 bg-gray-200 rounded-full h-2 mx-2">
              <div class="bg-yellow-400 h-2 rounded-full transition-all duration-300"
                   [style.width.%]="reviewBreakdown.percentage"></div>
            </div>
            <span class="text-sm text-gray-600 w-8 text-right">{{ reviewBreakdown.count }}</span>
          </div>
        }
      </div>
    </div>
  `,
  styles: ``,
})
export class RatingSummary {
  reviews = input.required<Review[]>();

  reviewCount = computed(() => this.reviews()!.length);
  ratingAvg = computed(
    () => (this.reviews()!.map((r) => r.rating)
      .reduce((acc, r) => acc + r)) / this.reviewCount());

  reviewsBreakdown = computed(() => {
    return [5,4,3,2,1].map(
      (star) => {
        const count = this.reviews().filter((r)=> Math.floor(r.rating)==star).length;
        const percentage = count / this.reviewCount() * 100;
        return {
          star,
          percentage,
          count
        }
      }
    )
  });

  protected readonly Math = Math;
}
