import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '@environments/environment';
import { Store } from '@shared/models/Store';
import { Observable } from 'rxjs';

export interface CreateStorePayload {
  name: string;
  description?: string;
  address?: string;
}

@Injectable({ providedIn: 'root' })
export class StoreApi {
  constructor(private http: HttpClient) {}

  getAllStores(): Observable<Store[]> {
    return this.http.get<Store[]>(`${environment.orderApiUrl}/api/stores`);
  }

  createStore(data: CreateStorePayload) {
    return this.http.post(`${environment.orderApiUrl}/api/stores`, data);
  }

  getStoreBySlug(slug: string): Observable<Store> {
    return this.http.get<Store>(`${environment.orderApiUrl}/api/stores/by-slug/${slug}`);
  }
}
