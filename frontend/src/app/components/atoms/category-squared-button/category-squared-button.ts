import { Component, input } from '@angular/core';
import { CategoryResponse } from '@shared/service/catalog-api';

@Component({
  selector: 'app-category-squared-button',
  imports: [],
  template: `
    <div class="flex flex-col w-[62px] h-[74px] gap-2 items-center justify-center">
      <div class="w-[26px] h-[26px] rounded-full overflow-hidden p-1 bg-[#F0EEF0]">
        <img
          class="cover rounded-full w-[18px] h-[18px]"
          [src]="category().imageUrl"
          [alt]="'Photo of ' + category().name"
        />
      </div>
      <span class="font-medium text-xs">{{ category().name }}</span>
    </div>
  `,
  styles: ``,
})
export class CategorySquaredButton {
  category = input.required<CategoryResponse>();
}
