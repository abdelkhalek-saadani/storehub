import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '@environments/environment';

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
export class SignupService {
  constructor(private http: HttpClient) {}

  signup(data: SignupPayload): Observable<SignupResponse> {
    return this.http.post<SignupResponse>(`${environment.orderApiUrl}/api/auth/signup`, data);
  }
}
