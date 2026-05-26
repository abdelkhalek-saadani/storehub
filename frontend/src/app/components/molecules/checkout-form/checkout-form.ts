import { Component } from '@angular/core';
import { MatDivider } from '@angular/material/divider';
import { MatInput, MatLabel } from '@angular/material/input';
import { PhoneInput } from '../../atoms/phone-input/phone-input';
import { MatFormField } from '@angular/material/form-field';

@Component({
  selector: 'app-checkout-form',
  imports: [MatDivider, MatFormField, MatInput, MatLabel, PhoneInput],
  template: `
    <div class="flex flex-col p-6 gap-6 border border-[#F0EEF0] rounded-xl">
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
  `,
  styles: ``,
})
export class CheckoutForm {}
