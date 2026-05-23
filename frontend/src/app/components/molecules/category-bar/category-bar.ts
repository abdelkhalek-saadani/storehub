import {Component, signal} from '@angular/core';
import {CategorySquaredButton} from '../../atoms/category-squared-button/category-squared-button';
import {MatChip, MatChipAvatar, MatChipSet} from '@angular/material/chips';
import {BreakpointObserver} from '@angular/cdk/layout';
import {takeUntilDestroyed} from '@angular/core/rxjs-interop';

@Component({
  selector: 'app-category-bar',
  imports: [
    CategorySquaredButton,
    MatChip,
    MatChipAvatar
  ],
  template: `
    <div class="flex flex-col items-stretch p-4 md:flex-row md:justify-center md:p-3">
      <div class="flex gap-2 justify-around md:gap-3 w-full">
        @for (i of [].constructor(5); track $index) {
          @if (isSquaredButton()) {
            <app-category-squared-button/>
          } @else {
            <mat-chip class="category-chip">
              <img
                matChipAvatar
                src="https://material.angular.dev/assets/img/examples/shiba1.jpg"
                alt="Photo of a Shiba Inu"
              />Bread
            </mat-chip>
          }
        }

      </div>
      <div class="flex gap-2 justify-around md:gap-3 w-full">

        @for (i of [].constructor(5); track $index) {
          @if (isSquaredButton()) {
            <app-category-squared-button/>
          } @else {
            <mat-chip class="category-chip">
              <img
                matChipAvatar
                src="https://material.angular.dev/assets/img/examples/shiba1.jpg"
                alt="Photo of a Shiba Inu"
              />Bread
            </mat-chip>
          }
        }

      </div>
    </div>
  `,
  styles: ``,
})
export class CategoryBar {
  isSquaredButton = signal(false);

  constructor(bpo: BreakpointObserver) {
    bpo
      .observe('(max-width: 1040px)')
      .pipe(takeUntilDestroyed())
      .subscribe(result => this.isSquaredButton.set(result.matches));
  }
}
