import { Component, input } from '@angular/core';
import { MatButton, MatIconButton } from '@angular/material/button';
import { MatIcon } from '@angular/material/icon';

@Component({
  selector: 'app-action-buttons',
  imports: [MatButton, MatIcon, MatIconButton],
  template: `
    @if (type() == 'desktop') {
      <div class="flex gap-6 items-center">
        <button matButton="filled" class="btn-md">
          <mat-icon> shopping_cart</mat-icon>
          Add To Cart
        </button>
        <button matIconButton class="delete">
          <mat-icon>delete</mat-icon>
        </button>
      </div>
    } @else {
      <div class="flex flex-col gap-6">
        <button matIconButton class="delete">
          <mat-icon>delete</mat-icon>
        </button>
        <button matIconButton class="add">
          <mat-icon>add</mat-icon>
        </button>
      </div>
    }
  `,
  styles: ``,
})
export class ActionButtons {
  type = input<'mobile' | 'desktop'>('desktop');
}
