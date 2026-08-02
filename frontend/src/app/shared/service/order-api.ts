import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '@environments/environment';
import { StoreContext } from '../../products/service/store-context';
import { UpdateCartItem } from '../../cart/model/update-cart-request';
import { CartResponse } from '../../cart/model/cart-response';

export interface OrderRequest {
  slotId: string;
  storeId: string;
  cartId: string;

  billingAddress?: string;
  deliveryAddress?: string;
  firstName?: string;
  lastName?: string;
  phoneNumber?: string;
}

export interface OrderResponse {
  orderId: string;
  paymentId: string;
  paymentApprovalUrl: string;
}

@Injectable({ providedIn: 'root' })
export class OrderApi {
  private http = inject(HttpClient);
  private cartUrl = `${environment.orderApiUrl}/api/cart`;
  private storeContext = inject(StoreContext);

  private getStoreId(): string {
    const storeId = this.storeContext.storeId();
    if (!storeId) {
      throw new Error('OrderApi called before storeId is available');
    }
    return storeId;
  }

  placeOrder(idemKey: string, request: Omit<OrderRequest, 'storeId'>): Observable<OrderResponse> {
    let headers = new HttpHeaders({ 'Idempotency-Key': idemKey });
    const response = this.http.post<OrderResponse>(
      `${environment.orderApiUrl}/api/orders`,
      { ...request, storeId: this.getStoreId() },
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

  clearCart(): Observable<CartResponse> {
    let params = new HttpParams().set('storeId', this.getStoreId());
    const response = this.http.delete<CartResponse>(this.cartUrl, { params });
    console.log(response);
    return response;
  }
}
