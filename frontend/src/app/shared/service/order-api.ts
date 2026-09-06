import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable, tap } from 'rxjs';
import { environment } from '@environments/environment';
import { StoreContext } from '../../store/service/store-context';
import { UpdateCartItem } from '../../cart/model/update-cart-request';
import { CartItemResponse, CartResponse } from '../../cart/model/cart-response';
import { LocalDateTime } from '@js-joda/core';
import { Order } from '../../models/Order';
import { ConfigService } from '@core/config.service';

export interface OrderRequest {
  slotId: string;
  cartId: string;

  billingAddress?: string;
  firstName?: string;
  lastName?: string;
  phoneNumber?: string;

  deliveryAddress?: {
    type: string;
    street: string;
    city: string;
    apartmentNumber?: string;
    zipCode: string;
    deliveryInstructions: string;
  };
}

export interface OrderCreatedResponse {
  orderId: string;
  paymentId: string;
  paymentApprovalUrl: string;
}

export interface OrderStatusDto {
  code: string;
  label: string;
}

export interface OrderCancelResponse {
  orderId: string;
  paymentId: string;
  orderStatus: OrderStatusDto;
  message: string;
}

export interface OrderItemResponse extends CartItemResponse {}

export interface OrderResponse {
  orderId: string;
  userId: string;
  email: string;
  storeId: string;
  originalTotal: number;
  finalTotal: number;
  totalDiscount: number;

  items: OrderItemResponse[];

  deliveryAddress: string;
  billingAddress: string;

  slotId: string;

  deliveryFee: string;

  status: OrderStatusDto;

  paymentId: string;

  paymentApprovalLink: string;

  createdAt: string;
}

@Injectable({ providedIn: 'root' })
export class OrderApi {
  private http = inject(HttpClient);
  private config = inject(ConfigService);
  private cartUrl = `${this.config.get().orderApiUrl}/api/cart`;
  private storeContext = inject(StoreContext);

  private getStoreId(): string {
    const storeId = this.storeContext.storeId();
    if (!storeId) {
      throw new Error('OrderApi called before storeId is available');
    }
    return storeId;
  }

  trackOrderStatus(orderId: string): Observable<OrderStatusDto> {
    return new Observable((subscriber) => {
      const es = new EventSource(`${this.config.get().orderApiUrl}/api/orders/${orderId}/track`);
      es.onmessage = (event) => subscriber.next(JSON.parse(event.data));
      es.onerror = (err) => subscriber.error(err);
      return () => es.close();
    });
  }

  placeOrder(idemKey: string, request: OrderRequest): Observable<OrderCreatedResponse> {
    let headers = new HttpHeaders({ 'Idempotency-Key': idemKey });
    const response = this.http.post<OrderCreatedResponse>(
      `${this.config.get().orderApiUrl}/api/orders`,
      { ...request, storeId: this.getStoreId() },
      { headers },
    );
    return response;
  }

  getOrder(token: string): Observable<OrderResponse> {
    const params = new HttpParams().set('paymentOrderId', token);
    const response = this.http.get<OrderResponse>(`${this.config.get().orderApiUrl}/api/orders`, {
      params,
    });
    return response;
  }

  getGuestOrder(orderId: string, email: string): Observable<OrderResponse> {
    const body = { orderId, email };
    const response = this.http.post<OrderResponse>(
      `${this.config.get().orderApiUrl}/api/orders/guest`,
      body,
      {},
    );
    return response;
  }

  cancelOrder(id: string, email: string | null): Observable<OrderCancelResponse> {
    let headers = new HttpHeaders();
    console.log(email);
    if (email) headers = headers.set('From', email);
    console.log(headers);
    const response = this.http.post<OrderCancelResponse>(
      `${this.config.get().orderApiUrl}/api/orders/${id}/void`,
      null,
      { headers },
    );
    return response;
  }

  getCart(): Observable<CartResponse> {
    let params = new HttpParams().set('storeId', this.getStoreId());

    const response = this.http.get<CartResponse>(this.cartUrl, { params });
    return response;
  }

  upsertItems(items: UpdateCartItem[]): Observable<CartResponse> {
    let requestBody = {
      storeId: this.getStoreId(),
      items: items,
    };
    const response = this.http.post<CartResponse>(`${this.cartUrl}/items`, requestBody);
    return response;
  }

  mergeCart() {
    let params = new HttpParams().set('storeId', this.getStoreId());
    const response = this.http.get<CartResponse>(`${this.cartUrl}/merge`, { params });
    return response;
  }

  clearCart(): Observable<CartResponse> {
    let params = new HttpParams().set('storeId', this.getStoreId());
    const response = this.http.delete<CartResponse>(this.cartUrl, { params });
    return response;
  }
}
