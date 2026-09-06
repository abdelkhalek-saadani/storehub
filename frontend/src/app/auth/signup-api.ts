import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '@environments/environment';
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
  constructor(
    private http: HttpClient,
    private config: ConfigService,
  ) {}

  signup(data: SignupPayload): Observable<SignupResponse> {
    return this.http.post<SignupResponse>(`${this.config.get().orderApiUrl}/api/auth/signup`, data);
  }
}
