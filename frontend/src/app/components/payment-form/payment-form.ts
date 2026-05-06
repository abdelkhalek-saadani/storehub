import { Component } from '@angular/core';
import {ViewPanel} from '../../directives/view-panel';
import {MatIcon} from '@angular/material/icon';
import {MatRadioButton, MatRadioGroup} from '@angular/material/radio';

@Component({
  selector: 'app-payment-form',
  imports: [
    ViewPanel,
    MatIcon,
    MatRadioGroup,
    MatRadioButton
  ],
  template: `
    <div appViewPanel class="border border-gray-200 rounded-xl p-6 bg-white">
      <h2 class="text-2xl font-bold mb-6 flex items-center gap-2">
        <mat-icon>payment</mat-icon>
        Payment Options
      </h2>

      <mat-radio-group>
        <mat-radio-button value="stripe" checked>
          <label>
            <img src="stripe-logo.png" alt="Stripe" class="h-6">
          </label>
        </mat-radio-button>
      </mat-radio-group>
    </div>

  `,
  styles: ``,
})
export class PaymentForm {

}
