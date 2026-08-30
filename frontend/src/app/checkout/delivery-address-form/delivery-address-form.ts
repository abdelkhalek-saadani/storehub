import { Component, inject, signal } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MatButton } from '@angular/material/button';

import { MatInput, MatLabel } from '@angular/material/input';
import { MatButtonToggle, MatButtonToggleGroup } from '@angular/material/button-toggle';
import { MatIcon } from '@angular/material/icon';
import { BreakpointObserver } from '@angular/cdk/layout';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';

import { MatFormFieldModule } from '@angular/material/form-field';
import { Breakpoints } from '@core/constants/breakpoints';
import { AddressType, CheckoutFormService } from '../checkout-form.service';

@Component({
  selector: 'app-delivery-address-form',
  imports: [
    MatButton,
    MatInput,
    MatLabel,
    MatButtonToggleGroup,
    MatButtonToggle,
    MatIcon,
    MatFormFieldModule,
    ReactiveFormsModule,
  ],
  template: `
    <div [formGroup]="checkoutForm" class="items-center gap-6 bg-white rounded-b-xl flex flex-col">
      <div formGroupName="deliveryAddress" class="w-full flex flex-col gap-6">
        <div>
          <h3 class="text-[#1a1b1f] font-medium mb-2 text-base">Select Address Type</h3>
          <mat-button-toggle-group formControlName="type">
            <mat-button-toggle [value]="AddressType.home">
              <mat-icon>home</mat-icon>
              Home
            </mat-button-toggle>
            <mat-button-toggle [value]="AddressType.apartment">
              <mat-icon>apartment</mat-icon>
              @if (isXSMobile()) {
                Apt.
              } @else {
                Apartment
              }
            </mat-button-toggle>
            <mat-button-toggle [value]="AddressType.office">
              <mat-icon>work</mat-icon>
              Office
            </mat-button-toggle>
          </mat-button-toggle-group>
        </div>
        <mat-form-field>
          <mat-label>Street Address</mat-label>
          <input matInput formControlName="street" data-cy="delivery-street-input" />
        </mat-form-field>

        <div class="flex flex-col gap-6 md:flex-row md:gap-3">
          <mat-form-field>
            <mat-label>City</mat-label>
            <input matInput formControlName="city" data-cy="delivery-city-input" />
          </mat-form-field>

          <mat-form-field>
            <mat-label>N.of Apartment</mat-label>
            <input matInput formControlName="apartmentNumber" data-cy="delivery-apartment-input" />
          </mat-form-field>

          <mat-form-field>
            <mat-label>Zip Code</mat-label>
            <input
              matInput
              type="number"
              formControlName="zipCode"
              data-cy="delivery-zipCode-input"
            />
          </mat-form-field>
        </div>
        <mat-form-field>
          <mat-label>Delivery Instructions</mat-label>
          <input
            matInput
            formControlName="deliveryInstructions"
            data-cy="delivery-instructions-input"
          />
        </mat-form-field>
      </div>
    </div>
  `,
  styles: ``,
})
export class DeliveryAddressForm {
  isXSMobile = signal(false);
  formService = inject(CheckoutFormService);
  checkoutForm = this.formService.form;

  constructor(bpo: BreakpointObserver) {
    bpo
      .observe(Breakpoints.xs)
      .pipe(takeUntilDestroyed())
      .subscribe((result) => this.isXSMobile.set(result.matches));
  }

  protected readonly AddressType = AddressType;
}
