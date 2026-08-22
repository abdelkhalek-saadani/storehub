import {
  Component,
  computed,
  effect,
  inject,
  input,
  ResourceRef,
  Signal,
  signal,
} from '@angular/core';
import { rxResource, takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { OrderApi, OrderResponse, OrderStatusDto } from '@shared/service/order-api';
import { InvoiceDownload } from '../invoice-download/invoice-download';
import { LocationSummary } from '../location-summary/location-summary';
import { MatButton } from '@angular/material/button';
import { MatProgressSpinner } from '@angular/material/progress-spinner';
import { OrderSummary } from '../order-summary/order-summary';
import { OrderTracking } from '../order-tracking/order-tracking';
import { PaymentSummary } from '../payment-summary/payment-summary';
import { ReviewOrder } from '@shared/components/review-order/review-order';
import { mapHttpError } from '../utility/error-mapping';
import { BreakpointObserver } from '@angular/cdk/layout';
import { Duration, LocalDateTime } from '@js-joda/core';
import { Slot } from '../track-order';
import { CatalogApi } from '@shared/service/catalog-api';
import { MatSnackBar } from '@angular/material/snack-bar';
import { Toaster } from '@shared/service/toaster';

@Component({
  selector: 'app-order-detail-view',
  imports: [
    OrderTracking,
    MatProgressSpinner,
    PaymentSummary,
    LocationSummary,
    OrderSummary,
    ReviewOrder,
    InvoiceDownload,
    MatButton,
  ],
  template: `
    @if (orderResult().error()) {
      {{ orderErrorMessage() }}
    } @else if (!orderResult().value().orderId) {
    } @else {
      @if (isMobile()) {
        @if (status(); as s) {
          <app-order-tracking
            [status]="s"
            [createdAt]="createdAt()"
            [orderArriveIn]="orderArriveIn()"
          />
        } @else {
          <mat-spinner></mat-spinner>
        }
        <app-payment-summary />
        <app-location-summary [deliveryAddress]="deliveryAddress()" />
        <app-order-summary [orderNumber]="orderNumber()" [itemsTotal]="itemsTotal()" />
        <app-review-order [items]="items()" />
        <app-invoice-download />
      } @else {
        <div class="flex gap-5">
          <div class="flex flex-col gap-6 w-2/3">
            @if (status(); as s) {
              <app-order-tracking
                [status]="s"
                [createdAt]="createdAt()"
                [orderArriveIn]="orderArriveIn()"
              />
            } @else {
              <mat-spinner></mat-spinner>
            }
            <app-review-order [items]="items()" />
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
          >You can cancel your order before its prepared</span
        >
        <button
          matButton="filled"
          class="danger w-full md:w-auto"
          (click)="cancelOrder()"
          [disabled]="isCancelling()"
        >
          {{ isCancelling() ? 'Cancelling...' : 'Cancel Order' }}
        </button>
      </div>
    }
  `,
})
export class OrderDetailView {
  orderResult = input.required<ResourceRef<OrderResponse>>();
  orderErrorMessage = computed(() => {
    const err = this.orderResult().error();
    if (!err) return null;
    return mapHttpError(err);
  });
  isMobile = signal(false);
  orderApi = inject(OrderApi);
  catalogApi = inject(CatalogApi);

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

  trackingResult = rxResource({
    params: () => {
      const id = this.orderId();
      return id ? { orderId: id } : undefined;
    },
    stream: ({ params }) => this.orderApi.trackOrderStatus(params.orderId),
  });

  status: Signal<OrderStatusDto | null> = computed(() => this.trackingResult.value() ?? null);

  createdAt = computed(() => {
    const caString = this.orderResult().value().createdAt;
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
    return this.orderResult().value().finalTotal;
  });
  slotId = computed(() => this.orderResult().value().slotId);
  slot = signal<Slot | null>(null);
  orderNumber = computed(() => {
    const order = this.orderResult().value();
    return order.orderId ? order.orderId : "Can't get order number";
  });
  isCancelling = signal(false);
  cancelOrder() {
    const id = this.orderResult().value().orderId;
    if (!id || this.isCancelling()) return;

    this.isCancelling.set(true);
    this.orderApi.cancelOrder(id).subscribe({
      next: () => {
        this.isCancelling.set(false);
        this.hotToaster.success('Order cancelled successfully');
        this.orderResult().reload();
      },
      error: (err) => {
        this.isCancelling.set(false);
        this.hotToaster.error('Failed to cancel order. Please try again.');
        console.error('Failed to cancel order', err);
      },
    });
  }
  orderId = computed(() => this.orderResult().value().orderId);
  private hotToaster = inject(Toaster);

  deliveryAddress = computed(() => {
    const da = this.orderResult().value().deliveryAddress;
    return da ? da : 'Cannot get the delivery address';
  });
  items = computed(() => {
    return this.orderResult().value().items;
  });
}
