import { Component } from '@angular/core';
import { OrderConfirmation } from '../order-confirmation/order-confirmation';

@Component({
  selector: 'app-cancel',
  imports: [OrderConfirmation],
  template: ` <app-order-confirmation state="fail"></app-order-confirmation>`,
  styles: ``,
})
export default class Cancel {}
