import {Component, computed, inject} from '@angular/core';
import {BackButton} from '../../components/back-button/back-button';
import {MatButton} from '@angular/material/button';
import {ShippingForm} from '../../components/shipping-form/shipping-form';
import {PaymentForm} from '../../components/payment-form/payment-form';
import {SummarizeOrder} from '../../components/summarize-order/summarize-order';
import {ProductState, ProductStore} from '../../product-store';
import {CartItem} from '../../models/CartItem';
import {DecimalPipe} from '@angular/common';
import {number} from 'zod';
import {MatTooltip} from '@angular/material/tooltip';
import {Router} from '@angular/router';


@Component({
  selector: 'app-checkout',
  imports: [
    BackButton,
    MatButton,
    ShippingForm,
    PaymentForm,
    SummarizeOrder,
    DecimalPipe,
    MatTooltip
  ],
  template: `
    <div class="mx-auto max-w-[1200px] py-6">
      <app-back-button navigateTo="/cart" class="block mb-4" label="Back to Cart"/>
      <h1 class="text-3xl font-extrabold mb-4">Checkout</h1>
      <div class="grid grid-cols-1 lg:grid-cols-5 gap-6">
        <div class="lg:col-span-3 flex flex-col gap-6">
          <app-shipping-form/>
          <app-payment-form/>
        </div>
        <div class="lg:col-span-2">
          <app-summarize-order>

            <!-- Order Lines -->
            <ng-container orderLines>
              <div class="space-y-2 border-b pb-4">
                @for (item of cartItems; track item.product.id) {
                  <div class="text-sm flex justify-between">
                    <span>{{ item.product.name }} x {{ item.qty }}</span>
                    <span>\${{ item.product.price * item.qty | number : '1.2-2' }}</span>
                  </div>
                }
              </div>
            </ng-container>

            <button matButton="filled" class="w-full mt-6 py-3"
                    [disabled]="!isLoggedIn() || store.isLoading()"
                    [disabledInteractive]="!isLoggedIn() || store.isLoading()"
                    [matTooltip]="!isLoggedIn()? 'You need to be logged in before placing an order!': ''"
                    (click)="placeOrder()">
              @if (store.isLoading()) {
                Processing the order...
              } @else {
                Place Order
              }
            </button>

          </app-summarize-order>
        </div>
      </div>
    </div>
  `,
  styles: ``,
})
export default class Checkout {
  store = inject(ProductStore);
  cartItems: CartItem[] = this.store.cartItems();
  isLoggedIn = computed(() => !!this.store.user());
  protected readonly number = number;
  router = inject(Router);
  async placeOrder()
  {
    await this.store.placeOrder();
    this.router.navigate(['/order-success']);
  }
}
