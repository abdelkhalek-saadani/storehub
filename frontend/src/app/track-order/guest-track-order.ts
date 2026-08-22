import { Component, DestroyRef, inject, OnInit, ResourceRef, signal } from '@angular/core';
import { MatIcon } from '@angular/material/icon';
import { MatButton, MatIconButton } from '@angular/material/button';
import { OrderDetailView } from './order-details-view/order-details-view';
import { ActivatedRoute } from '@angular/router';
import { OrderApi, OrderResponse } from '@shared/service/order-api';
import { rxResource, takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { MatError, MatFormField, MatInput, MatLabel } from '@angular/material/input';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';

export interface formValue {
  email: string;
  orderId: string;
}

@Component({
  selector: 'app-guest-track-order',
  imports: [
    MatIcon,
    MatIconButton,
    OrderDetailView,
    MatInput,
    ReactiveFormsModule,
    MatButton,
    MatFormField,
    MatError,
    MatLabel,
  ],
  host: {
    class: 'min-h-screen flex flex-col px-4 pb-10 bg-[#FEFCFE] ',
  },
  template: `
    <div class="flex flex-col gap-4">
      <div class="py-5 flex items-center gap-2">
        <button matIconButton class="back-button">
          <mat-icon>arrow_back</mat-icon>
        </button>
        <span class="font-semibold text-[20px]">Track Your Order</span>
      </div>

      <form [formGroup]="form" (ngSubmit)="lookup()" class="flex flex-col gap-3 md:flex-row">
        <div class="flex-2">
          <mat-form-field class="flex-2">
            <mat-label>Order ID</mat-label>
            <input matInput placeholder="xxxx-xxxx-xxx-xxxxx" formControlName="orderId" />
            @if (isTouched('orderId') && isInvalid('orderId')) {
              <mat-error>Order ID is required</mat-error>
            }
          </mat-form-field>
        </div>
        <div class="flex-2">
          <mat-form-field>
            <mat-label>Email</mat-label>
            <input matInput placeholder="example@example.com" formControlName="email" />
            @if (isTouched('email') && isInvalid('email')) {
              <mat-error>Valid email is required</mat-error>
            }
          </mat-form-field>
          @if (orderResult.error()) {
            <span class="text-red-600 text-sm">No matching order found</span>
          }
        </div>
        <div class="flex-1 pt-[3px]">
          <button
            matButton="filled"
            type="submit"
            [disabled]="orderResult.isLoading()"
            class="w-full"
          >
            {{ orderResult.isLoading() ? 'Searching...' : 'Find' }}
          </button>
        </div>
      </form>

      @if (!orderResult.error()) {
        <app-order-detail-view [orderResult]="orderResult" />
      }
    </div>
  `,
})
export default class GuestTrackOrderPage implements OnInit {
  private fb = inject(FormBuilder);
  private route = inject(ActivatedRoute);
  private orderApi = inject(OrderApi);

  form = this.fb.nonNullable.group({
    orderId: ['', [Validators.required]],
    email: ['', [Validators.required, Validators.email]],
  });

  isTouched(field: keyof formValue): boolean {
    return this.form.controls[field].touched;
  }

  isInvalid(field: keyof formValue): boolean {
    return this.form.controls[field].invalid;
  }

  searchParams = signal<{ orderId: string; email: string } | undefined>(undefined);

  ngOnInit() {
    const id = this.route.snapshot.queryParamMap.get('orderId');
    if (id) this.form.patchValue({ orderId: id });
  }

  lookup() {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
    this.searchParams.set({ ...this.form.getRawValue() });
  }

  submitted = signal(false);

  destroyRef = inject(DestroyRef);
  email = signal('');
  orderId = signal<string | null>('');

  orderResult: ResourceRef<OrderResponse> = rxResource({
    params: () => this.searchParams(),
    stream: ({ params }) => this.orderApi.getGuestOrder(params.orderId, params.email),
    defaultValue: {
      orderId: '',
      userId: '',
      storeId: '',
      originalTotal: 0,
      finalTotal: 0,
      totalDiscount: 0,
      items: [],
      deliveryAddress: '',
      billingAddress: '',
      slotId: '',
      deliveryFee: '',
      status: { code: '', label: '' },
      paymentId: '',
      paymentApprovalLink: '',
      createdAt: '',
    },
  });
}
