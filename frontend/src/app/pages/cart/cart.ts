import {Component, inject} from '@angular/core';
import {BackButton} from '../../components/back-button/back-button';
import {ProductStore} from '../../product-store';
import {TeaseWishlist} from '../../components/tease-wishlist/tease-wishlist';
import {ListCartItems} from './list-cart-items/list-cart-items';
import {SummarizeOrder} from '../../components/summarize-order/summarize-order';
import {MatButton} from '@angular/material/button';
import {Router} from '@angular/router';
import {Dialog} from '@angular/cdk/dialog';
import {SignInDialog} from '../../components/sign-in-dialog/sign-in-dialog';
import {MatDialog} from '@angular/material/dialog';

@Component({
  selector: 'app-cart',
  imports: [
    BackButton,
    TeaseWishlist,
    ListCartItems,
    SummarizeOrder,
    MatButton
  ],
  template: `
    <div class="mx-auto max-w-[1200px] py-6">
      <app-back-button [navigateTo]="'/products/'+ store.category()" class="block mb-6" />
      <h1 class="text-3xl font-extrabold mb-4">Shopping Cart</h1>
      <app-tease-wishlist class="block mb-6" />
      <div class="grid grid-cols-1 lg:grid-cols-3 gap-6">
        <div class="lg:col-span-2">
          <app-list-cart-items [cartItems]="store.cartItems()"/>
        </div>
        <div>
          <app-summarize-order>
            <button matButton="filled" class="w-full mt-6 py-3" (click)="proceedToCheckout()">
              Proceed to Checkout
            </button>
          </app-summarize-order>
        </div>
      </div>
    </div>
  `,
  styles: ``,
})
export default class Cart {
  store = inject(ProductStore);
  router = inject(Router)
  dialog = inject(MatDialog);
  proceedToCheckout() {
    if (this.store.user()) this.router.navigate(['/checkout']);
    else {
      const dialogRef = this.dialog.open(SignInDialog, {
        disableClose: true,
        data: {redirect: true ,redirectTo: "/checkout"}
      })

    }
  }
}
