import { Component } from '@angular/core';
import { WishlistProductCard } from '@components/molecules/wishlist-product-card/wishlist-product-card';
import { MatIcon } from '@angular/material/icon';
import { MatIconButton } from '@angular/material/button';

@Component({
  selector: 'app-wishlist',
  imports: [WishlistProductCard, MatIcon, MatIconButton],
  host: {
    class: 'min-h-screen flex flex-col gap-6 px-4 py-4 bg-[#FEFCFE]',
  },
  template: `
    <div
      class="bg-white flex justify-between p-6 items-center rounded-[32px] border border-[#F8F7F8] "
    >
      <div class="flex gap-6 items-center">
        <div
          class="flex items-center justify-center  rounded-full bg-[#FEF5FD] w-[50px] aspect-square"
        >
          <div class="text-primary text-2xl flex">
            <mat-icon [inline]="true">favorite</mat-icon>
          </div>
        </div>
        <span class="text-[20px] font-semibold">My wishlist</span>
      </div>
      <button matIconButton class="back-button no-border">
        <mat-icon>chevron_left</mat-icon>
      </button>
    </div>
    <div class="px-3 py-6 md:p-6 bg-white flex flex-col rounded-[32px] border border-[#F8F7F8]">
      <div class="text-[#686069] font-semibold text-[14px] md:text-lg">
        <span>Items</span>
      </div>
      <div class="flex flex-col md:gap-2 divide-y divide-gray-200">
        @for (i of [].constructor(3); track $index) {
          <app-wishlist-product-card />
        }
      </div>
    </div>
  `,
})
export default class WishlistPage {}
