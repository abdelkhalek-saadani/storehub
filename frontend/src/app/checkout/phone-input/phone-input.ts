import { Component, inject, OnInit, ViewChild } from '@angular/core';
import IntlTelInput, { intlTelInput } from '@intl-tel-input/angular';
// import 'intl-tel-input/styles';
import { CheckoutFormService } from '../checkout-form.service';
import type { ValidationError } from 'intl-tel-input';
import { ReactiveFormsModule } from '@angular/forms';
@Component({
  selector: 'app-phone-input',
  imports: [IntlTelInput, ReactiveFormsModule],
  template: `
    <div [formGroup]="checkoutForm">
      <intl-tel-input
        #telInput
        formControlName="phone"
        [inputAttributes]="{
          class:
            'h-[52px] ' +
            'rounded-lg ' +
            'text-base ' +
            'border ' +
            'border-mat-sys-outline ' +
            'focus:border-blue-500 ' +
            'focus:border-2 ' +
            'focus:outline-none',
        }"
        class="w-full block flex items-center justify-center"
        initialCountry="tn"
        [loadUtils]="loadUtils"
      ></intl-tel-input>
      <div class="text-red-500">
        @if (phone?.errors?.['required'] && phone?.touched) {
          Phone number is required.
        } @else if (phone?.errors?.['invalidPhone'] && phone?.touched) {
          {{ getErrorMessage(phone?.errors?.['invalidPhone']) }}
        }
      </div>
    </div>
  `,
  styles: `
    intl-tel-input .iti {
      width: 100%;
    }
  `,
})
export class PhoneInput {
  loadUtils = () => import('intl-tel-input/utils');
  @ViewChild('telInput') telInput!: IntlTelInput;

  checkoutForm = inject(CheckoutFormService).form;

  get phone() {
    return this.checkoutForm.get('phone');
  }

  getErrorMessage(errorCode: ValidationError | null): string {
    const { VALIDATION_ERROR } = intlTelInput;
    switch (errorCode) {
      case VALIDATION_ERROR.INVALID_COUNTRY_CODE:
        return 'Invalid dial code';
      case VALIDATION_ERROR.TOO_SHORT:
        return 'Too short';
      case VALIDATION_ERROR.TOO_LONG:
        return 'Too long';
      default:
        return 'Invalid number';
    }
  }
}
