import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '@environments/environment';

export interface CreateStorePayload {
  name: string;
  description?: string;
  address?: string;
}

@Injectable({ providedIn: 'root' })
export class StoreApi {
  constructor(private http: HttpClient) {}

  createStore(data: CreateStorePayload) {
    return this.http.post(`${environment.apiUrl}/api/stores`, data);
  }
}
