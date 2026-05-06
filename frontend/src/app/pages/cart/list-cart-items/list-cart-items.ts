import {Component, inject, input, Input} from '@angular/core';
import {ViewPanel} from '../../../directives/view-panel';
import {ProductStore} from '../../../product-store';
import {ShowCartItem} from '../../show-cart-item/show-cart-item';
import {CartItem} from '../../../models/CartItem';

@Component({
  selector: 'app-list-cart-items',
  imports: [
    ViewPanel,
    ShowCartItem
  ],
  template: `
    <div appViewPanel class="border border-gray-200 rounded-xl p-6 bg-white"><h2
      class="text-2xl font-bold mb-4">Cart Items ({{store.cartItemsCounter()}})</h2>
      <div class="flex flex-col gap-6">
        @for (ci of cartItems(); track $index) {
          <app-show-cart-item [cartItem]="ci" />
        }

        </div>
    </div>
  `,
  styles: ``,
})
export class ListCartItems {
 store = inject(ProductStore);
 cartItems = input.required<CartItem[]>()
}
