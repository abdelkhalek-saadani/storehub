import { inject, Injectable, signal } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { OrderApi } from '@shared/service/order-api';
import { LocalDate } from '@js-joda/core';
import { v4 as uuidv4 } from 'uuid';
import { CartStore } from '../cart/cart-store';
import { NavigationService } from './navigation.service';

export enum AddressType {
  home = 'HOME',
  office = 'OFFICE',
  apartment = 'APARTMENT',
}

@Injectable({ providedIn: 'root' })
export class CheckoutFormService {
  form = new FormGroup({
    phone: new FormControl<string>('23725059', [Validators.required]),
    deliveryDay: new FormControl<LocalDate | null>(null),
    firstName: new FormControl('Abdelkhalek', {
      nonNullable: true,
      validators: [Validators.required],
    }),
    lastName: new FormControl('Saadani', { nonNullable: true, validators: [Validators.required] }),
    email: new FormControl('abdelkhalek@gmail.com', {
      nonNullable: true,
      validators: [Validators.required, Validators.email],
    }),
    slotId: new FormControl('', { nonNullable: true, validators: [Validators.required] }),
    deliveryAddress: new FormGroup({
      type: new FormControl(AddressType.home, {
        nonNullable: true,
        validators: [Validators.required],
      }),
      street: new FormControl('zuhur street', {
        nonNullable: true,
        validators: [Validators.required],
      }),
      city: new FormControl('zayatine city', {
        nonNullable: true,
        validators: [Validators.required],
      }),
      apartmentNumber: new FormControl('22'),
      zipCode: new FormControl('8030'),
      deliveryInstructions: new FormControl('Jawk behi'),
    }),
  });
  idempotencyKey = uuidv4();
  cartStore = inject(CartStore);
  navigationService = inject(NavigationService);

  submitting = signal(false);
  submitError = signal<string | null>(null);

  private orderApi = inject(OrderApi);

  submit() {
    if (this.form.invalid || this.submitting()) {
      this.form.markAllAsTouched();
      return;
    }
    this.submitting.set(true);
    this.submitError.set(null);

    const value = this.form.getRawValue();

    const cartId = this.cartStore.cartId();
    if (!cartId) {
      this.submitError.set('Your cart could not be found. Please refresh and try again.');
      this.submitting.set(false);
      return;
    }
    const da = value.deliveryAddress;
    const body = {
      slotId: value.slotId,
      cartId,
      firstName: value.firstName,
      lastName: value.lastName,
      email: value.email,
      deliveryAddress: {
        type: da.type,
        street: da.street,
        city: da.city,
        apartmentNumber: da.apartmentNumber ?? '',
        zipCode: da.zipCode ?? '',
        deliveryInstructions: da.deliveryInstructions ?? '',
      },
    };
    this.orderApi.placeOrder(this.idempotencyKey, body).subscribe({
      next: (res) => {
        this.navigationService.redirectTo(res.paymentApprovalUrl);
      },
      error: () => {
        this.submitting.set(false);
        this.submitError.set('Something went wrong. Please try again.');
      },
    });
  }
}
