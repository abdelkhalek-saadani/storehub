import { Component } from '@angular/core';
import { OrderConfirmation } from '../../checkout/order-confirmation/order-confirmation';

@Component({
  selector: 'app-payment-failed',
  imports: [OrderConfirmation],
  host: {
    class: 'min-h-screen flex items-center justify-center ',
  },
  template: `
    <div class="w-full max-w-md -mt-40">
      <app-order-confirmation state="fail" />
    </div>
  `,
})
export default class PaymentFailedPage {}
