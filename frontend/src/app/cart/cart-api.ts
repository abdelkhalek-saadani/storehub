import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '@environments/environment';
import { StoreContext } from '../products/service/store-context';
import { UpdateCartItem } from './model/update-cart-request';
import { CartResponse } from './model/cart-response';

@Injectable({ providedIn: 'root' })
export class CartApi {
  private http = inject(HttpClient);
  private cartUrl = `${environment.orderApiUrl}/api/cart`;
  private storeContext = inject(StoreContext);

  private getStoreId(): string {
    const storeId = this.storeContext.storeId();
    if (!storeId) {
      throw new Error('CartApi called before storeId is available');
    }
    return storeId;
  }

  getCart(): Observable<CartResponse> {
    let params = new HttpParams().set('storeId', this.getStoreId());

    const response = this.http.get<CartResponse>(this.cartUrl, { params });
    console.log(response);
    return response;
  }

  upsertItems(items: UpdateCartItem[]): Observable<CartResponse> {
    let requestBody = {
      storeId: this.getStoreId(),
      items: items,
    };
    const response = this.http.post<CartResponse>(`${this.cartUrl}/items`, requestBody);
    console.log(response);
    return response;
  }

  clearCart(): Observable<CartResponse> {
    let params = new HttpParams().set('storeId', this.getStoreId());
    const response = this.http.delete<CartResponse>(this.cartUrl, { params });
    console.log(response);
    return response;
  }
}
