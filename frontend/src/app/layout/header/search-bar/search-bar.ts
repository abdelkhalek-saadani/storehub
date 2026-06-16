import { Component } from '@angular/core';
import { MatIcon } from '@angular/material/icon';

@Component({
  selector: 'app-search-bar',
  imports: [MatIcon],
  template: `
    <div class="flex items-center gap-2 bg-[#F8F7F8] rounded-2xl p-4 h-12">
      <div class="bg-red text-primary h-6 w-6 text-[24px] leading-[24px]">
        <mat-icon [inline]="true">search</mat-icon>
      </div>
      <input
        type="text"
        placeholder="Search..."
        class="bg-transparent outline-none text-sm text-gray-700 placeholder-gray-400 w-full"
      />
    </div>
  `,
  styles: ``,
})
export class SearchBar {}
