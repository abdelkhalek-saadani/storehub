import { Component, inject, signal } from '@angular/core';
import { MatDivider } from '@angular/material/divider';
import { MatError, MatInput, MatLabel } from '@angular/material/input';
import { MatFormField } from '@angular/material/form-field';
import { PhoneInput } from '../phone-input/phone-input';
import { MatOption, MatSelect } from '@angular/material/select';
import { rxResource, toSignal } from '@angular/core/rxjs-interop';
import { CatalogApi } from '@shared/service/catalog-api';

import { tap } from 'rxjs';
import { ReactiveFormsModule } from '@angular/forms';

import { CheckoutFormService } from '../checkout-form.service';

interface CheckoutFormValue {
  firstName: string;
  lastName: string;
  email: string;
  slotId: string;
}

@Component({
  selector: 'app-checkout-form',
  imports: [
    MatDivider,
    MatFormField,
    MatInput,
    MatLabel,
    PhoneInput,
    MatOption,
    MatSelect,
    ReactiveFormsModule,
    MatError,
  ],
  template: `
    <form [formGroup]="checkoutForm">
      <div class="flex flex-col p-6 gap-6 border border-[#F0EEF0] rounded-xl">
        <div class="flex gap-2 justify-center items-center">
          <mat-form-field>
            <mat-label>Delivery Day</mat-label>
            <mat-select formControlName="deliveryDay">
              @for (date of week(); track $index) {
                <mat-option [value]="date.date">{{ date.day }}</mat-option>
              }
            </mat-select>
          </mat-form-field>

          <mat-form-field>
            <mat-label>Time Slot</mat-label>
            <mat-select formControlName="slotId">
              @for (slot of slotsResult.value(); track slot.slotId) {
                <mat-option [value]="slot.slotId">{{ slot.slotLabel }}</mat-option>
              }
            </mat-select>
          </mat-form-field>
        </div>
        <div class="font-semibold text-lg">Your Details</div>
        <mat-form-field>
          <mat-label>Email Address</mat-label>
          <input matInput formControlName="email" />
          @if (checkoutForm.controls.email.hasError('required') && isTouched('email')) {
            <mat-error>Email is required.</mat-error>
          }
          @if (checkoutForm.controls.email.hasError('email') && isTouched('email')) {
            <mat-error>Enter a valid email.</mat-error>
          }
        </mat-form-field>
        <mat-divider />
        <mat-form-field>
          <mat-label>First Name</mat-label>
          <input matInput formControlName="firstName" />
          @if (isInvalid('firstName')) {
            <mat-error>First name is required.</mat-error>
          }
        </mat-form-field>
        <mat-form-field>
          <mat-label>Last Name</mat-label>
          <input matInput formControlName="lastName" />
          @if (isInvalid('lastName')) {
            <mat-error>Last name is required.</mat-error>
          }
        </mat-form-field>
        <app-phone-input />
      </div>
    </form>
  `,
  styles: ``,
})
export class CheckoutForm {
  productService = inject(CatalogApi);
  formService = inject(CheckoutFormService);
  checkoutForm = this.formService.form;

  private deliveryDayCtrl = this.checkoutForm.controls.deliveryDay;
  private timeSlotCtrl = this.checkoutForm.controls.slotId;

  isInvalid(field: keyof CheckoutFormValue): boolean {
    const control = this.checkoutForm.controls[field];
    return control.invalid && control.touched;
  }

  isTouched(field: keyof CheckoutFormValue): boolean {
    return this.checkoutForm.controls[field].touched;
  }

  week = toSignal(
    this.productService.getDays().pipe(
      tap((days) => {
        if (days.length > 0 && !this.deliveryDayCtrl.value) {
          this.deliveryDayCtrl.setValue(days[0].date);
        }
      }),
    ),
    { initialValue: [] },
  );

  date = toSignal(this.deliveryDayCtrl.valueChanges, {
    initialValue: this.deliveryDayCtrl.value,
  });

  slotsResult = rxResource({
    params: () => {
      const d = this.date();
      return d ? { date: d } : undefined;
    },
    stream: ({ params }) =>
      this.productService
        .getSlots(params.date)
        .pipe(tap((slots) => this.timeSlotCtrl.setValue(slots[0]?.slotId ?? ''))),
    defaultValue: [],
  });
}
