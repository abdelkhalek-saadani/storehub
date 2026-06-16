import { Component } from '@angular/core';
import { OrderConfirmation } from '@components/molecules/order-confirmation/order-confirmation';

@Component({
  selector: 'app-payment-success',
  imports: [OrderConfirmation],
  host: {
    class: 'min-h-screen flex items-center justify-center ',
  },
  template: `
    <div class="w-full max-w-md -mt-40">
      <app-order-confirmation state="success" />
    </div>
  `,
})
export default class PaymentSuccessPage {}
