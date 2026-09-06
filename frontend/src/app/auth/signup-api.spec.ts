import { TestBed } from '@angular/core/testing';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { SignupApi, SignupPayload, SignupResponse } from './signup-api';
import { provideHttpClient } from '@angular/common/http';
import { provideZonelessChangeDetection } from '@angular/core';
import { ConfigService } from '@core/config.service';

describe('SignupApi', () => {
  let api: SignupApi;
  let httpMock: HttpTestingController;
  let configServiceSpy: jasmine.SpyObj<ConfigService>;

  beforeEach(() => {
    configServiceSpy = jasmine.createSpyObj('ConfigService', ['get']);
    configServiceSpy.get.and.returnValue({
      orderApiUrl: 'http://localhost:8080',
      catalogApiUrl: 'http://localhost:8080',
      kcUrl: 'http://localhost:8088',
    });

    TestBed.configureTestingModule({
      providers: [
        provideZonelessChangeDetection(),
        SignupApi,
        provideHttpClient(),
        provideHttpClientTesting(),
      ],
    });
    api = TestBed.inject(SignupApi);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(api).toBeTruthy();
  });

  it('should POST to the signup endpoint with the correct payload', () => {
    const payload: SignupPayload = {
      email: 'test@example.com',
      password: 'password123',
      firstName: 'John',
      lastName: 'Doe',
      address: '123 Main St',
      phoneNumber: '12345678',
    };
    const mockResponse: SignupResponse = { message: 'Success' };

    api.signup(payload).subscribe((res) => {
      expect(res).toEqual(mockResponse);
    });

    const req = httpMock.expectOne(`http://localhost:8080/api/auth/signup`);
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual(payload);

    req.flush(mockResponse);
  });

  it('should propagate an error response', () => {
    const payload: SignupPayload = {
      email: 'dup@example.com',
      password: 'password123',
      firstName: 'John',
      lastName: 'Doe',
      address: '123 Main St',
      phoneNumber: '12345678',
    };

    api.signup(payload).subscribe({
      next: () => fail('expected an error'),
      error: (err) => expect(err.status).toBe(409),
    });

    const req = httpMock.expectOne(`http://localhost:8080/api/auth/signup`);
    req.flush({ message: 'Email already exists' }, { status: 409, statusText: 'Conflict' });
  });
});
