import { Component } from '@angular/core';
import { OrderConfirmation } from '../order-confirmation/order-confirmation';

@Component({
  selector: 'app-cancel',
  imports: [OrderConfirmation],
  host: {
    class: 'min-h-screen flex items-center justify-center',
  },
  template: ` <div class="w-full max-w-md -mt-40">
    <app-order-confirmation state="fail"></app-order-confirmation>
  </div>`,
  styles: ``,
})
export default class Cancel {}
