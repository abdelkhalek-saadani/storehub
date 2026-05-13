import { Component } from '@angular/core';

@Component({
  selector: 'app-category-squared-button',
  imports: [],
  template: `
    <div class="flex flex-col w-[62px] h-[74px] gap-2 items-center justify-center">
      <div class="w-[26px] h-[26px] rounded-full overflow-hidden p-1 bg-[#F0EEF0]">
        <img
          class="cover rounded-full w-[18px] h-[18px]"
          src="https://material.angular.dev/assets/img/examples/shiba1.jpg"
          alt="Photo of a Shiba Inu"
        />
      </div>
      <span class="font-medium text-xs">Bread</span>
    </div>
  `,
  styles: ``,
})
export class CategorySquaredButton {

}
