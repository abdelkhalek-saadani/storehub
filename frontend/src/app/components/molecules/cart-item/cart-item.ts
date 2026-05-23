import { Component } from '@angular/core';
import { MatButton, MatIconButton } from '@angular/material/button';
import { MatIcon } from '@angular/material/icon';

@Component({
  selector: 'app-cart-item',
  imports: [MatButton, MatIcon, MatIconButton],
  template: `
    <div class="flex flex-col pb-4 gap-3 border-b border-[#F8F7F8]">
      <div class="flex flex-row gap-5 items-center">
        <div class="rounded-2xl h-26 aspect-square overflow-hidden">
          <img src="product-img-placeholder.jpg" alt="Natural Milk" />
        </div>
        <div class="flex flex-col gap-3">
          <div class="text-base font-medium">Some product name</div>
          <div class="flex flex-row gap-3">
            <div class="text-xl font-semibold text-primary">5.00 DT</div>
            <div class="text-xl font-regular text-[#A2A2A2] line-through">6.00 DT</div>
          </div>
        </div>
      </div>
      <div class="flex flex-row gap-6 justify-between items-center">
        <div class="font-semibold text-base">8.00 DT</div>
        <div class="flex flex-row gap-4 items-center">
          <button matButton="text" class="sidecart-button">Remove</button>
          <div class="flex flex-row items-center bg-[#F6FAFF]">
            <button matIconButton class="sidecart-button">
              <mat-icon>remove</mat-icon>
            </button>
            <div class="text-sm font-medium px-1">4</div>
            <button matIconButton class="sidecart-button">
              <mat-icon>add</mat-icon>
            </button>
          </div>
        </div>
      </div>
    </div>
  `,
  styles: ``,
})
export class CartItem {}
