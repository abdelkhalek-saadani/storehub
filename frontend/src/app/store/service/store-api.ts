import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '@environments/environment';
import { Store } from '@shared/models/Store';
import { Observable } from 'rxjs';
import { ConfigService } from '@core/config.service';

export interface CreateStorePayload {
  name: string;
  description?: string;
  address?: string;
}

@Injectable({ providedIn: 'root' })
export class StoreApi {
  constructor(
    private http: HttpClient,
    private config: ConfigService,
  ) {}

  getAllStores(): Observable<Store[]> {
    return this.http.get<Store[]>(`${this.config.get().orderApiUrl}/api/stores`);
  }

  createStore(data: CreateStorePayload) {
    return this.http.post(`${this.config.get().orderApiUrl}/api/stores`, data);
  }

  getStoreBySlug(slug: string): Observable<Store> {
    return this.http.get<Store>(`${this.config.get().orderApiUrl}/api/stores/by-slug/${slug}`);
  }
}
