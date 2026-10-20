import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Store } from '@shared/models/Store';
import { Observable } from 'rxjs';
import { ConfigService } from '@core/config.service';

export interface CreateStorePayload {
  name: string;
  description?: string;
  address?: string;
}

export interface CreateStoreResponse {
  id: string;
  name: string;
  description: string;
  address: string;
  slug: string;
}

@Injectable({ providedIn: 'root' })
export class StoreApi {
  private http = inject(HttpClient);
  private config = inject(ConfigService);

  getAllStores(): Observable<Store[]> {
    return this.http.get<Store[]>(`${this.config.get().orderApiUrl}/api/stores`);
  }

  createStore(data: CreateStorePayload): Observable<CreateStoreResponse> {
    return this.http.post<CreateStoreResponse>(`${this.config.get().orderApiUrl}/api/stores`, data);
  }

  getStoreBySlug(slug: string): Observable<Store> {
    return this.http.get<Store>(`${this.config.get().orderApiUrl}/api/stores/by-slug/${slug}`);
  }
}
