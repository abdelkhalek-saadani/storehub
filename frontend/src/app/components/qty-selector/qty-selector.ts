import {Component, inject, input, output} from '@angular/core';
import {MatIconButton} from '@angular/material/button';
import {MatIcon} from '@angular/material/icon';
import {CartItem} from '../../models/CartItem';
import {ProductStore} from '../../product-store';
import {Product} from '../../models/Product';

@Component({
  selector: 'app-qty-selector',
  imports: [
    MatIconButton,
    MatIcon
  ],
  template: `
    <div class="flex items-center gap-3">
      <div class="inline-flex items-center">
        <button
          matIconButton
          [disabled]="quantity()==1"
          class="bg-primary"
        >
          <mat-icon (click)="qtyUpdated.emit('decrement')">
            remove
          </mat-icon>

        </button>
        <div class="px-3">{{ quantity() }}</div>
        <button matIconButton class="bg-primary">
          <mat-icon (click)="qtyUpdated.emit('increment')">
            add
          </mat-icon>
        </button>
      </div>
    </div>
  `,
  styles: ``,
})
export class QtySelector {

  quantity = input<number>();
  qtyUpdated = output<UpdateNature>();

  store= inject(ProductStore);
}

export type UpdateNature = 'increment' | 'decrement'
