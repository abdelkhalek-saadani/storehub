import {Component, computed, inject} from '@angular/core';
import {ProductStore} from '../../product-store';
import {ViewPanel} from '../../directives/view-panel';
import {MatButton} from '@angular/material/button';
import {DecimalPipe} from '@angular/common';

@Component({
  selector: 'app-summarize-order',
  imports: [
    ViewPanel,
    DecimalPipe
  ],
  template: `
    <div appViewPanel class="border border-gray-200 rounded-xl p-6 bg-white"><h2
      class="text-2xl font-bold mb-4">Order Summary</h2>
      <ng-content select="[orderLines]"/>
      <div class="space-y-3 text-lg pt-4">
        <div class="flex justify-between"><span>Subtotal</span><span>\${{ subtotal() | number : '1.2-2' }}</span></div>
        <div class="flex justify-between"><span>Tax</span><span>$0.00</span></div>
        <div class="border-t pt-3 mt-3 flex justify-between font-bold text-lg">
          <span>Total</span><span>\${{ subtotal() | number : '1.2-2' }}</span></div>
      </div>
      <ng-content select="button"/>
    </div>
  `,
  styles: ``,
})
export class SummarizeOrder {
  store = inject(ProductStore);
  subtotal = computed(
    () => this.store.cartItems().reduce(
      (subtotal, ci) => subtotal += ci.product.price * ci.qty,
      0
    )
  );
}
