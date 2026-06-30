import { Component, computed, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { HttpClient } from '@angular/common/http';
import Keycloak from 'keycloak-js';
import { MatFormField, MatLabel } from '@angular/material/form-field';
import { MatInput } from '@angular/material/input';
import { MatButton } from '@angular/material/button';
import { Divider } from '@components/atoms/divider/divider';
import { Router } from '@angular/router';
import { LogoText } from '@components/atoms/logo-text/logo-text';
import { MatButtonToggle, MatButtonToggleGroup } from '@angular/material/button-toggle';
import { MatIcon } from '@angular/material/icon';
import { toSignal } from '@angular/core/rxjs-interop';
import { SignupService } from '../signup-api';
import { PendingStoreStorage } from '../pending-store-storage';
import { lastValueFrom } from 'rxjs';

@Component({
  selector: 'app-signup',
  standalone: true,
  imports: [
    ReactiveFormsModule,
    MatLabel,
    MatInput,
    MatFormField,
    MatButton,
    LogoText,
    MatButtonToggle,
    MatButtonToggleGroup,
    MatIcon,
  ],
  host: {
    class: 'min-h-screen flex flex-col bg-primary',
  },
  template: `
    <div class="flex items-center justify-center h-16 bg-white border border-[#F0EEF0] ">
      <app-logo-text
        [width]="145.94"
        [height]="34.85"
        (click)="router.navigateByUrl('/products')"
        class="cursor-pointer"
      />
    </div>
    <div class="flex items-center justify-center grow px-4 my-4">
      <div class="flex flex-col max-w-[418px] min-w-[300px] md:min-w-[418px] gap-4">
        <div class="p-6 bg-white rounded-2xl border border-[#F8F7F8]">
          <span class="text-2xl font-bold">Create an account</span>
          <form [formGroup]="form" (ngSubmit)="onSubmit()" class="pt-6 flex flex-col gap-4">
            <div class="flex flex-col gap-4 md:gap-2 md:flex-row">
              <mat-form-field appearance="outline">
                <mat-label>First Name</mat-label>
                <input matInput formControlName="firstName" value="Abdelkhalek" />
              </mat-form-field>

              <mat-form-field appearance="outline">
                <mat-label>Last Name</mat-label>
                <input matInput formControlName="lastName" />
              </mat-form-field>
            </div>
            <div class="flex flex-col gap-4 md:gap-2 md:flex-row">
              <mat-form-field appearance="outline">
                <mat-label>Address</mat-label>
                <input matInput formControlName="address" />
              </mat-form-field>

              <mat-form-field appearance="outline">
                <mat-label>Phone Number</mat-label>
                <input matInput formControlName="phoneNumber" />
              </mat-form-field>
            </div>

            <mat-form-field appearance="outline">
              <mat-label>Email</mat-label>
              <input matInput formControlName="email" placeholder="Email" />
            </mat-form-field>

            <mat-form-field appearance="outline">
              <mat-label>Password</mat-label>
              <input matInput formControlName="password" type="password" placeholder="Password" />
            </mat-form-field>

            <mat-button-toggle-group formControlName="accountType" class="w-fit">
              <mat-button-toggle value="customer">
                <mat-icon>boy</mat-icon>
                Customer
              </mat-button-toggle>
              <mat-button-toggle value="owner">
                <mat-icon>local_convenience_store</mat-icon>
                Owner
              </mat-button-toggle>
            </mat-button-toggle-group>

            @if (isOwner()) {
              <mat-form-field appearance="outline">
                <mat-label>Store Name</mat-label>
                <input matInput formControlName="storeName" placeholder="Super Mart" />
              </mat-form-field>
              <mat-form-field appearance="outline">
                <mat-label>Description</mat-label>
                <input matInput formControlName="storeDescription" placeholder="Email" />
              </mat-form-field>
              <mat-form-field appearance="outline">
                <mat-label>Address</mat-label>
                <input matInput formControlName="storeAddress" placeholder="address" />
              </mat-form-field>
            }

            <button
              mat-raised-button
              color="primary"
              type="submit"
              [disabled]="form.invalid || submitting()"
            >
              Sign up
            </button>
          </form>
        </div>
      </div>
    </div>
  `,
})
export class Signup {
  private readonly fb = inject(FormBuilder);
  private readonly keycloak = inject(Keycloak);
  readonly router = inject(Router);

  private signupService = inject(SignupService);
  private pendingStoreStorage = inject(PendingStoreStorage);

  errorMessage = '';
  isSubmitting = false;

  form = this.fb.group({
    firstName: ['Abdelkhalek', Validators.required],
    lastName: ['Saadani', Validators.required],
    address: ['cite Zayatine', Validators.required],
    phoneNumber: ['23000999', Validators.required],
    email: ['abdelkhaleksaadani@gmail.com', [Validators.required, Validators.email]],
    password: ['password', [Validators.required, Validators.minLength(8)]],
    accountType: ['customer', Validators.required],
    storeName: ['Super market'],
    storeDescription: ['A description'],
    storeAddress: ['cite zayatine'],
  });

  async onSubmit() {
    if (this.form.invalid) return;
    this.isSubmitting = true;
    this.errorMessage = '';

    const values = this.form.value;

    try {
      // Step 1: create the account (identity fields only go to backend)
      await lastValueFrom(
        this.signupService.signup({
          email: values.email!,
          password: values.password!,
          firstName: values.firstName!,
          lastName: values.lastName!,
          address: values.address ?? '',
          phoneNumber: values.phoneNumber ?? '',
        }),
      );

      // Step 2: if owner, stash the store data for after login completes
      if (values.accountType === 'owner') {
        this.pendingStoreStorage.savePendingStore({
          name: values.storeName!,
          description: values.storeDescription ?? '',
          address: values.storeAddress ?? '',
        });
      }

      // Step 3: trigger real login, pre-filled email, user just enters password
      await this.keycloak.login({
        loginHint: values.email!,
        redirectUri: window.location.origin + '/post-login',
      });
    } catch (err: any) {
      this.errorMessage = err?.error?.message ?? 'Signup failed';
      this.isSubmitting = false;
    }
  }

  submitting = signal(false);

  type = toSignal(this.form.controls.accountType.valueChanges, {
    initialValue: this.form.controls.accountType.value,
  });
  isOwner = computed(() => this.type() === 'owner');
}
