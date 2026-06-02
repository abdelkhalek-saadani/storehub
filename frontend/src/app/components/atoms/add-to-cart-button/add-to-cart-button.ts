import { Component, inject, input, output } from '@angular/core';
import { MatIconButton } from '@angular/material/button';
import { MatIcon } from '@angular/material/icon';
import { ProductStore } from '../../../product-store';

@Component({
  selector: 'app-add-to-cart-button',
  imports: [MatIconButton, MatIcon],
  template: `
    <div class="flex items-center gap-3 bg-white rounded-full">
      <div class="inline-flex items-center">
        @if (quantity() != 0) {
          <button matIconButton [disabled]="quantity() == 1" class="remove">
            <mat-icon (click)="qtyUpdated.emit('decrement')"> remove </mat-icon>
          </button>
          <div class="px-3 text-sm font-medium">{{ quantity() }}</div>
        }
        <button matIconButton class="add">
          <mat-icon (click)="qtyUpdated.emit('increment')"> add </mat-icon>
        </button>
      </div>
    </div>
  `,
  styles: ``,
})
export class AddToCartButton {
  quantity = input<number>();
  qtyUpdated = output<UpdateNature>();

  store = inject(ProductStore);
}

export type UpdateNature = 'increment' | 'decrement';
