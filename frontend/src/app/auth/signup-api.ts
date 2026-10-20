import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ConfigService } from '@core/config.service';

export interface SignupPayload {
  email: string;
  password: string;
  firstName: string;
  lastName: string;
  address: string;
  phoneNumber: string;
}

export interface SignupResponse {
  message: string;
}

@Injectable({ providedIn: 'root' })
export class SignupApi {
  private http = inject(HttpClient);
  private config = inject(ConfigService);

  signup(data: SignupPayload): Observable<SignupResponse> {
    return this.http.post<SignupResponse>(`${this.config.get().orderApiUrl}/api/auth/signup`, data);
  }
}
