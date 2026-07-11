import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '@environments/environment';
import { StoreContext } from '../products/service/store-context';
import { PagedResponse } from '../products/models/page-response';
import { ProductQuery } from '../products/service/catalog-api';

export interface CartQuery {
  page: number;
  size: number;
  categories?: string[];
  minPrice?: number;
  maxPrice?: number;
}

@Injectable({ providedIn: 'root' })
export class CartApi {
  private http = inject(HttpClient);
  private cartUrl = `${environment.orderApiUrl}/api/cart`;
  private categoriesUrl = `${environment.catalogApiUrl}/categories`;
  private storeContext = inject(StoreContext);

  private getStoreId(): string {
    const storeId = this.storeContext.storeId();
    if (!storeId) {
      throw new Error('ProductService called before storeId is available');
    }
    return storeId;
  }

  getProducts(query: ProductQuery): Observable<PagedResponse<Product>> {
    let params = new HttpParams()
      .set('storeId', this.getStoreId())
      .set('page', query.page)
      .set('size', query.size);

    if (query.categories?.length) {
      params = params.set('categories', query.categories.join(','));
    }
    if (query.minPrice != null) {
      params = params.set('minPrice', query.minPrice);
    }
    if (query.maxPrice != null) {
      params = params.set('maxPrice', query.maxPrice);
    }

    const response = this.http.get<PagedResponse<Product>>(this.productsUrl, { params });
    console.log(response);
    return response;
  }

  getCategories(): Observable<string[]> {
    const params = new HttpParams().set('storeId', this.getStoreId());
    return this.http.get<string[]>(this.categoriesUrl, { params });
  }
}
