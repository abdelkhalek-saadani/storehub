import { Component, effect, inject, OnInit, signal } from '@angular/core';
import { MatDivider } from '@angular/material/divider';
import { MatInput, MatLabel } from '@angular/material/input';
import { MatFormField } from '@angular/material/form-field';
import { PhoneInput } from '../phone-input/phone-input';
import { MatOption, MatSelect } from '@angular/material/select';
import { rxResource, toSignal } from '@angular/core/rxjs-interop';
import { PagedResponse } from '../../products/models/page-response';
import { Product } from '../../products/models/product';
import { ProductQuery, CatalogApi } from '@shared/service/catalog-api';
import { Slot } from '@shared/models/Slot';
import { DayOfWeek, LocalDate } from '@js-joda/core';
import { tap } from 'rxjs';
import { DateAndDay } from '@shared/models/DateAndDay';
import { FormControl, FormGroup, ReactiveFormsModule } from '@angular/forms';

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
            <mat-select formControlName="timeSlot">
              @for (slot of slotsResult.value(); track slot.slotId) {
                <mat-option [value]="slot.slotId">{{ slot.slotLabel }}</mat-option>
              }
            </mat-select>
          </mat-form-field>
        </div>
        <div class="font-semibold text-lg">Your Details</div>
        <mat-form-field>
          <mat-label>Email Address</mat-label>
          <input matInput />
        </mat-form-field>
        <mat-divider />
        <mat-form-field>
          <mat-label>First Name</mat-label>
          <input matInput />
        </mat-form-field>
        <mat-form-field>
          <mat-label>Last Name</mat-label>
          <input matInput />
        </mat-form-field>
        <app-phone-input />
      </div>
    </form>
  `,
  styles: ``,
})
export class CheckoutForm {
  productService = inject(CatalogApi);

  checkoutForm = new FormGroup({
    deliveryDay: new FormControl<LocalDate | null>(null),
    timeSlot: new FormControl(''),
  });

  private deliveryDayCtrl = this.checkoutForm.controls.deliveryDay;
  private timeSlotCtrl = this.checkoutForm.controls.timeSlot;

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
