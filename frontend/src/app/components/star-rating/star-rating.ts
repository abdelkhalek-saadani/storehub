import {Component, input} from '@angular/core';
import {MatIcon} from '@angular/material/icon';

@Component({
  selector: 'app-star-rating',
  imports: [
    MatIcon
  ],
  template: `
    <div class="flex items-center">
      <div class="flex items-center mr-2">
        @for (s of stars; track $index) {
          <mat-icon
            [inline]="true"
            class="!text-lg"
            [class]="s ? '!text-yellow-400' : '!text-gray-300'"
          >
            star
          </mat-icon>
        }
        <ng-content/>
      </div>
    </div>
  `,
  styles: ``,
})
export class StarRating {
  rate = input<number>();
  stars : boolean[] = [];

  ngOnInit (){
    this.stars = [1,2,3,4,5].map((i) => this.rate()! >= i);
  }

}
