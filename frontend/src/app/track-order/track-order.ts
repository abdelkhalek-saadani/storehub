import {
  Component,
  computed,
  DestroyRef,
  effect,
  inject,
  OnInit,
  ResourceRef,
  Signal,
  signal,
} from '@angular/core';
import { MatButton, MatIconButton } from '@angular/material/button';
import { MatIcon } from '@angular/material/icon';
import { rxResource, takeUntilDestroyed } from '@angular/core/rxjs-interop';

import { ActivatedRoute } from '@angular/router';
import { OrderApi, OrderResponse, OrderStatusDto } from '@shared/service/order-api';

import { Duration, LocalDateTime } from '@js-joda/core';
import { OrderDetailView } from './order-details-view/order-details-view';

export interface Slot {
  startTime: LocalDateTime;
  endTime: LocalDateTime;
}

@Component({
  selector: 'app-track-order',
  imports: [MatIcon, MatIconButton, OrderDetailView],
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

      <app-order-detail-view [orderResult]="orderResult" />
    </div>
  `,
})
export default class TrackOrderPage implements OnInit {
  private route = inject(ActivatedRoute);
  private orderApi = inject(OrderApi);
  token = signal<string | null>(null);
  destroyRef = inject(DestroyRef);

  orderResult: ResourceRef<OrderResponse> = rxResource({
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
}
