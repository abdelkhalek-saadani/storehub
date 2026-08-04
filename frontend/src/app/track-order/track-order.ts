import { Component, computed, DestroyRef, effect, inject, OnInit, signal } from '@angular/core';
import { OrderTracking } from './order-tracking/order-tracking';
import { MatButton, MatIconButton } from '@angular/material/button';
import { MatIcon } from '@angular/material/icon';
import { PaymentSummary } from './payment-summary/payment-summary';
import { LocationSummary } from './location-summary/location-summary';
import { OrderSummary } from './order-summary/order-summary';
import { InvoiceDownload } from './invoice-download/invoice-download';
import { BreakpointObserver } from '@angular/cdk/layout';
import { rxResource, takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { ReviewOrder } from '@shared/components/review-order/review-order';
import { ActivatedRoute } from '@angular/router';
import { OrderApi } from '@shared/service/order-api';

import { Duration, LocalDateTime } from '@js-joda/core';
import { CatalogApi } from '@shared/service/catalog-api';
import { raw } from 'express';
import { HttpErrorResponse } from '@angular/common/http';
import { mapHttpError } from './error-mapping';

export interface Slot {
  startTime: LocalDateTime;
  endTime: LocalDateTime;
}

@Component({
  selector: 'app-track-order',
  imports: [
    OrderTracking,
    ReviewOrder,
    MatButton,
    MatIcon,
    PaymentSummary,
    LocationSummary,
    OrderSummary,
    InvoiceDownload,
    MatIconButton,
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
        <span class="font-semibold text-[20px]">Order Details</span>
      </div>

      @if (orderResult.error()) {
        {{ orderErrorMessage() }}
      } @else {
        @if (isMobile()) {
          <app-order-tracking
            [status]="status()"
            [createdAt]="createdAt()"
            [orderArriveIn]="orderArriveIn()"
          />
          <app-payment-summary />
          <app-location-summary [deliveryAddress]="deliveryAddress()" />
          <app-order-summary [orderNumber]="orderNumber()" [itemsTotal]="itemsTotal()" />
          <app-review-order />
          <app-invoice-download />
        } @else {
          <div class="flex gap-5">
            <div class="flex flex-col gap-6 w-2/3">
              <app-order-tracking
                [status]="status()"
                [createdAt]="createdAt()"
                [orderArriveIn]="orderArriveIn()"
              />
              <app-review-order />
            </div>
            <div class="flex flex-col gap-4 w-1/3">
              <app-order-summary [orderNumber]="orderNumber()" [itemsTotal]="itemsTotal()" />
              <app-payment-summary />
              <app-location-summary [deliveryAddress]="deliveryAddress()" />
              <app-invoice-download />
            </div>
          </div>
        }

        <div
          class=" md:mt-8 p-6 md:p-8 flex flex-col md:flex-row md:justify-between items-center gap-4 border-[#F8F7F8] rounded-2xl bg-white"
        >
          <span class="font-medium text-[14px] md:text-base"
            >You can can your order before its prepared</span
          >
          <button matButton="filled" class="danger w-full md:w-auto">Cancel Order</button>
        </div>
      }
    </div>
  `,
})
export default class TrackOrderPage implements OnInit {
  isMobile = signal(false);
  private route = inject(ActivatedRoute);
  private orderApi = inject(OrderApi);
  private catalogApi = inject(CatalogApi);
  token = signal<string | null>(null);
  destroyRef = inject(DestroyRef);

  slotId = computed(() => this.orderResult.value().slotId);

  constructor(bpo: BreakpointObserver) {
    bpo
      .observe('(max-width: 768px)')
      .pipe(takeUntilDestroyed())
      .subscribe((res) => this.isMobile.set(res.matches));

    effect(() => {
      const id = this.slotId();
      if (!id) return;
      this.catalogApi.getSlotById(id).subscribe((slot) => {
        this.slot.set({
          startTime: LocalDateTime.parse(slot.startTime),
          endTime: LocalDateTime.parse(slot.endTime),
        });
      });
    });
  }

  items = computed(() => {
    return this.orderResult.value().items;
  });

  status = computed(() => this.orderResult.value().status);

  createdAt = computed(() => {
    const caString = this.orderResult.value().createdAt;
    if (!caString) {
      return new Date();
    }
    const createdAt = LocalDateTime.parse(caString);
    return new Date(
      createdAt.year(),
      createdAt.monthValue() - 1,
      createdAt.dayOfMonth(),
      createdAt.hour(),
      createdAt.minute(),
    );
  });

  slot = signal<Slot | null>(null);

  orderArriveIn = computed(() => {
    const slot = this.slot();
    if (slot == null) return null;

    const now = LocalDateTime.now();
    const duration = Duration.between(now, slot.endTime);

    if (duration.isNegative()) return 'Arriving soon';

    const days = duration.toDays();
    if (days > 0) return `Arrive in ${days} day${days > 1 ? 's' : ''}`;

    const hours = duration.toHours();
    if (hours > 0) return `Arrive in ${hours} hour${hours > 1 ? 's' : ''}`;

    const minutes = duration.toMinutes();
    return `Arrive in ${minutes} minute${minutes !== 1 ? 's' : ''}`;
  });

  itemsTotal = computed(() => {
    return this.orderResult.value().finalTotal;
  });

  deliveryAddress = computed(() => {
    const da = this.orderResult.value().deliveryAddress;
    return da ? da : 'Cannot get the delivery address';
  });

  orderNumber = computed(() => {
    const order = this.orderResult.value();
    return order ? order.orderId : "Can't get order number";
  });

  orderResult = rxResource({
    params: () => {
      const t = this.token();
      return t ? { token: t } : undefined;
    },
    stream: ({ params }) => this.orderApi.getOrder(params.token),
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

  ngOnInit() {
    this.route.queryParamMap.pipe(takeUntilDestroyed(this.destroyRef)).subscribe((paramMap) => {
      this.token.set(paramMap.get('token'));
    });
  }

  orderErrorMessage = computed(() => {
    const err = this.orderResult.error();
    if (!err) return null;
    return mapHttpError(err);
  });
}
