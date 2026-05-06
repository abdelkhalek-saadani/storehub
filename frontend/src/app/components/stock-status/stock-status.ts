import { Component } from '@angular/core';
import {MatIcon} from '@angular/material/icon';

@Component({
  selector: 'app-stock-status',
  imports: [
    MatIcon
  ],
  template: `
    <div class="flex items-center gap-2 border border-gray-200 rounded-lg px-3 py-3 bg-white w-full">
      <mat-icon class="!text-green-500">
        check_circle
      </mat-icon>
      <span class="text-xs text-gray-800">In stock and ready to ship</span></div>
  `,
  styles: ``,
})
export class StockStatus {

}
