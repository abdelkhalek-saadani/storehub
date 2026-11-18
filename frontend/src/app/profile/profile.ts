import { Component, effect, inject, signal } from '@angular/core';
import { NonNullableFormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatFormField, MatLabel } from '@angular/material/form-field';
import { MatSelect } from '@angular/material/select';
import {
  MatDatepicker,
  MatDatepickerInput,
  MatDatepickerToggle,
} from '@angular/material/datepicker';
import { MatSuffix } from '@angular/material/form-field';
import { MatOption } from '@angular/material/autocomplete';
import { MatCard } from '@angular/material/card';
import { MatIcon } from '@angular/material/icon';
import { MatError, MatInput } from '@angular/material/input';
import { MatButton } from '@angular/material/button';
import { rxResource } from '@angular/core/rxjs-interop';
import { OrderApi } from '@shared/service/order-api';
import { tap, finalize } from 'rxjs';
import { Toaster } from '@shared/service/toaster';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-profile',
  imports: [
    MatOption,
    MatLabel,
    MatSelect,
    MatDatepickerToggle,
    MatDatepicker,
    MatSuffix,
    MatCard,
    MatIcon,
    ReactiveFormsModule,
    MatFormField,
    MatInput,
    MatDatepickerInput,
    MatButton,
    MatError,
    RouterLink,
  ],
  host: {
    class: 'min-h-screen flex items-center justify-center ',
  },
  template: `
    <div class="min-h-screen bg-gray-50 flex justify-center p-4 md:p-8">
      <mat-card class="w-full max-w-2xl !p-6">
        <h1 class="text-2xl font-semibold mb-6">Edit Profile</h1>

        <div class="flex flex-col items-center gap-4 mb-6">
          <img
            src="avatar.png"
            alt="Avatar"
            class="w-20 h-20 rounded-full object-cover bg-gray-200"
          />
          <label class="cursor-pointer">
            <input type="file" accept="image/*" class="hidden" (change)="onAvatar($event)" />
            <span mat-stroked-button>
              <mat-icon>upload</mat-icon>
            </span>
          </label>
        </div>

        <form [formGroup]="form" (ngSubmit)="save()" class="grid grid-cols-1 md:grid-cols-2 gap-4">
          <mat-form-field appearance="outline">
            <mat-label>First name</mat-label>
            <input matInput formControlName="firstName" />
            <mat-error>Required</mat-error>
          </mat-form-field>

          <mat-form-field appearance="outline">
            <mat-label>Last name</mat-label>
            <input matInput formControlName="lastName" />
            <mat-error>Required</mat-error>
          </mat-form-field>

          <mat-form-field appearance="outline" class="md:col-span-2">
            <mat-label>Email</mat-label>
            <input [readonly]="true" matInput type="email" formControlName="email" />
            <mat-error>Enter a valid email</mat-error>
          </mat-form-field>

          <mat-form-field appearance="outline">
            <mat-label>Phone</mat-label>
            <input matInput type="tel" formControlName="phoneNumber" />
          </mat-form-field>

          <mat-form-field appearance="outline">
            <mat-label>Date of birth</mat-label>
            <input matInput [matDatepicker]="dp" formControlName="dateOfBirth" />
            <mat-datepicker-toggle matIconSuffix [for]="dp"></mat-datepicker-toggle>
            <mat-datepicker #dp></mat-datepicker>
          </mat-form-field>

          <mat-form-field appearance="outline">
            <mat-label>Gender</mat-label>
            <mat-select formControlName="gender">
              <mat-option value="male">Male</mat-option>
              <mat-option value="female">Female</mat-option>
            </mat-select>
          </mat-form-field>

          <mat-form-field appearance="outline">
            <mat-label>Country</mat-label>
            <input matInput formControlName="country" />
          </mat-form-field>

          <mat-form-field appearance="outline" class="md:col-span-2">
            <mat-label>Address</mat-label>
            <input matInput formControlName="address" />
          </mat-form-field>

          <mat-form-field appearance="outline">
            <mat-label>City</mat-label>
            <input matInput formControlName="city" />
          </mat-form-field>

          <mat-form-field appearance="outline">
            <mat-label>Postal code</mat-label>
            <input matInput formControlName="postalCode" />
          </mat-form-field>

          <div class="md:col-span-2 flex justify-end gap-3 mt-2 items-center">
            <button matButton="text" type="button" routerLink="/products-explorer">Cancel</button>
            <button matButton="filled" type="submit" [disabled]="form.invalid || saving()">
              {{ saving() ? 'Saving...' : 'Save changes' }}
            </button>
          </div>
        </form>
      </mat-card>
    </div>
  `,
})
export default class ProfilePage {
  private fb = inject(NonNullableFormBuilder);
  private orderApi = inject(OrderApi);

  avatar = 'https://i.pravatar.cc/150';

  constructor() {
    effect(() => {
      const user = this.userDefaults.value();
      if (user) this.form.reset(user);
    });
  }

  onAvatar(e: Event) {
    const file = (e.target as HTMLInputElement).files?.[0];
    if (file) this.avatar = URL.createObjectURL(file);
  }

  userDefaults = rxResource<ProfileDetails, unknown>({
    stream: () => this.orderApi.getCurrentUser().pipe(tap((user) => console.log(user))),
  });

  initial = {
    firstName: this.userDefaults.value()?.firstName,
    lastName: this.userDefaults.value()?.lastName,
    email: this.userDefaults.value()?.email,
    phoneNumber: this.userDefaults.value()?.phoneNumber,
    dateOfBirth: this.userDefaults.value()?.dateOfBirth as Date,
    gender: this.userDefaults.value()?.gender,
    country: this.userDefaults.value()?.country,
    address: this.userDefaults.value()?.address,
    city: this.userDefaults.value()?.city,
    postalCode: this.userDefaults.value()?.postalCode,
  };

  form = this.fb.group({
    firstName: [this.initial.firstName, Validators.required],
    lastName: [this.initial.lastName, Validators.required],
    email: [this.initial.email, [Validators.required, Validators.email]],
    phoneNumber: [this.initial.phoneNumber],
    dateOfBirth: [this.initial.dateOfBirth],
    gender: [this.initial.gender],
    country: [this.initial.country],
    address: [this.initial.address],
    city: [this.initial.city],
    postalCode: [this.initial.postalCode],
  });

  toaster = inject(Toaster);
  saving = signal(false);
  save() {
    if (this.form.invalid || this.saving()) return;

    this.saving.set(true);

    const changes = this.form.getRawValue();

    this.orderApi
      .updateUser(changes)
      .pipe(finalize(() => this.saving.set(false)))
      .subscribe({
        next: (user) => {
          this.form.reset(user); // new baseline, marks pristine
          this.toaster.success('Profile updated', 3000);
        },
        error: (err) => {
          const message =
            err.status === 400
              ? 'Please check your input.'
              : err.status === 401
                ? 'Session expired. Please log in again.'
                : 'Could not save profile. Please try again.';
          this.toaster.error(message);
        },
      });
  }
}

export interface ProfileDetails {
  firstName: string;
  lastName: string;
  email: string;
  phoneNumber: string;
  dateOfBirth: Date;
  gender: string;
  country: string;
  address: string;
  city: string;
  postalCode: string;
}
