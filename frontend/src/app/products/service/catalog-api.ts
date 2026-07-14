import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '@environments/environment';
import { PagedResponse } from '../models/page-response';
import { Product } from '../models/product';
import { StoreContext } from './store-context';
import { List } from 'postcss/lib/list';
import { Offer } from '@shared/models/Offer';

export interface ProductQuery {
  page: number;
  size: number;
  categories?: string[];
  minPrice?: number;
  maxPrice?: number;
  saleEvent?: string;
  isBestSeller?: boolean;
}

export interface CategoryResponse {
  id: string;
  name: string;
  imageUrl: string;
}

@Injectable({ providedIn: 'root' })
export class ProductService {
  private http = inject(HttpClient);
  private productsUrl = `${environment.catalogApiUrl}/api/products`;
  private categoriesUrl = `${environment.catalogApiUrl}/api/categories/subcategories`;
  private saleEventsUrl = `${environment.catalogApiUrl}/api/sale-events`;
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

    if (query.saleEvent != null) {
      params = params.set('saleEvent', query.saleEvent);
    }
    if (query.isBestSeller) {
      params = params.set('isBestSeller', query.isBestSeller);
    }

    const response = this.http.get<PagedResponse<Product>>(this.productsUrl, { params });
    console.log(response);
    return response;
  }

  getBestSellerProducts(): Observable<Product[]> {
    let params = new HttpParams().set('storeId', this.getStoreId()).set('isBestSeller', true);

    const response = this.http.get<Product[]>(`${this.productsUrl}/explorer`, { params });
    console.log(response);
    return response;
  }

  getSaleEvent(): Observable<Offer[]> {
    let params = new HttpParams().set('storeId', this.getStoreId()).set('count', 6);

    const response = this.http.get<Offer[]>(this.saleEventsUrl, { params });
    console.log(response);
    return response;
  }

  getCategories(count: number): Observable<CategoryResponse[]> {
    const params = new HttpParams().set('storeId', this.getStoreId()).set('count', count);
    return this.http.get<CategoryResponse[]>(this.categoriesUrl, { params });
  }
}
