import { Component, input } from '@angular/core';
import { MatIcon } from '@angular/material/icon';
import { StarRating } from '../star-rating/star-rating';
import { ViewPanel } from '../../directives/view-panel';
import { Review } from '../../models/Review';

@Component({
  selector: 'app-view-review-item',
  imports: [StarRating, ViewPanel],
  template: `
    <div appViewPanel="" class="border border-gray-200 rounded-xl p-6 bg-white">
      <div class="flex items-start gap-4">
        <img class="w-10 h-10 rounded-full" [src]="review()?.avatar" [alt]="review()?.name" />
        <div>
          <div class="text-lg font-semibold">
            {{ review()?.name }}
          </div>
          <div class="flex items-center mb-1">
            <app-star-rating [rate]="review()?.rating">
              <div class="text-sm text-gray-500">
                {{ review()?.date }}
              </div>
            </app-star-rating>
          </div>
          <div class="text-base font-semibold mb-1">
            {{ review()?.title }}
          </div>
          <div class="text-sm text-gray-500">
            {{ review()?.comment }}
          </div>
        </div>
      </div>
    </div>
  `,
  styles: ``,
})
export class ViewReviewItem {
  review = input<Review>();
}
