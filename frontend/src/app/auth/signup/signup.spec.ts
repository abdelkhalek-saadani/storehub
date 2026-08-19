import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { of, throwError } from 'rxjs';
import Keycloak from 'keycloak-js';
import { Router } from '@angular/router';
import { Signup } from './signup';
import { SignupApi } from '../signup-api';
import { PendingStoreStorage } from '../pending-store-storage';
import { provideZonelessChangeDetection } from '@angular/core';

describe('Signup', () => {
  let component: Signup;
  let fixture: ComponentFixture<Signup>;
  let signupServiceSpy: jasmine.SpyObj<SignupApi>;
  let pendingStoreStorageSpy: jasmine.SpyObj<PendingStoreStorage>;
  let keycloakSpy: jasmine.SpyObj<Keycloak>;
  let routerSpy: jasmine.SpyObj<Router>;

  beforeEach(async () => {
    signupServiceSpy = jasmine.createSpyObj('SignupService', ['signup']);
    pendingStoreStorageSpy = jasmine.createSpyObj('PendingStoreStorage', ['savePendingStore']);
    keycloakSpy = jasmine.createSpyObj('Keycloak', ['login']);
    routerSpy = jasmine.createSpyObj('Router', ['navigateByUrl', 'navigate']);

    TestBed.configureTestingModule({
      imports: [Signup, ReactiveFormsModule],
      providers: [
        provideZonelessChangeDetection(),
        { provide: SignupApi, useValue: signupServiceSpy },
        { provide: PendingStoreStorage, useValue: pendingStoreStorageSpy },
        { provide: Keycloak, useValue: keycloakSpy },
        { provide: Router, useValue: routerSpy },
      ],
    });

    fixture = TestBed.createComponent(Signup);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  describe('isOwner computed signal', () => {
    it('is false by default (customer)', () => {
      expect(component.isOwner()).toBeFalse();
    });

    it('becomes true when accountType changes to owner', () => {
      component.form.controls.accountType.setValue('owner');
      expect(component.isOwner()).toBeTrue();
    });
  });

  describe('form validity', () => {
    it('is invalid when a required field(e.g. email) is empty', () => {
      component.form.controls.email.setValue('');
      expect(component.form.invalid).toBeTrue();
    });

    it('is invalid with a malformed email', () => {
      component.form.controls.email.setValue('not-an-email');
      expect(component.form.controls.email.valid).toBeFalse();
    });

    it('is invalid with a password under 8 characters', () => {
      component.form.controls.password.setValue('short');
      expect(component.form.controls.password.valid).toBeFalse();
    });

    it('is valid with the seeded default values', () => {
      expect(component.form.valid).toBeTrue();
    });
  });

  describe('onSubmit', () => {
    it('does not call signupService when the form is invalid', async () => {
      component.form.controls.email.setValue('');
      await component.onSubmit();
      expect(signupServiceSpy.signup).not.toHaveBeenCalled();
    });

    it('calls signupService.signup with the mapped identity payload', async () => {
      signupServiceSpy.signup.and.returnValue(of({ message: 'ok' }));
      keycloakSpy.login.and.returnValue(Promise.resolve());

      await component.onSubmit();

      const values = component.form.getRawValue();
      expect(signupServiceSpy.signup).toHaveBeenCalledWith({
        email: values.email,
        password: values.password,
        firstName: values.firstName,
        lastName: values.lastName,
        address: values.address,
        phoneNumber: values.phoneNumber,
      });
    });

    it('saves a pending store when accountType is owner', async () => {
      component.form.controls.accountType.setValue('owner');
      signupServiceSpy.signup.and.returnValue(of({ message: 'ok' }));
      keycloakSpy.login.and.returnValue(Promise.resolve());

      await component.onSubmit();
      const formValues = component.form.getRawValue();
      expect(pendingStoreStorageSpy.savePendingStore).toHaveBeenCalledWith({
        name: formValues.storeName,
        description: formValues.storeDescription,
        address: formValues.storeAddress,
      });
    });

    it('does NOT save a pending store when accountType is customer', async () => {
      signupServiceSpy.signup.and.returnValue(of({ message: 'ok' }));
      keycloakSpy.login.and.returnValue(Promise.resolve());

      await component.onSubmit();

      expect(pendingStoreStorageSpy.savePendingStore).not.toHaveBeenCalled();
    });

    it('calls keycloak.login with loginHint and redirectUri on success', async () => {
      signupServiceSpy.signup.and.returnValue(of({ message: 'ok' }));
      keycloakSpy.login.and.returnValue(Promise.resolve());

      await component.onSubmit();

      expect(keycloakSpy.login).toHaveBeenCalledWith({
        loginHint: component.form.value.email,
        redirectUri: window.location.origin + '/post-login',
      });
    });

    it('sets errorMessage and resets isSubmitting when signup fails', async () => {
      signupServiceSpy.signup.and.returnValue(
        throwError(() => ({ error: { message: 'Email already exists' } })),
      );

      await component.onSubmit();

      expect(component.errorMessage).toBe('Email already exists');
      expect(component.isSubmitting()).toBeFalse();
    });

    it('falls back to a generic error message when none is provided', async () => {
      signupServiceSpy.signup.and.returnValue(throwError(() => ({})));

      await component.onSubmit();

      expect(component.errorMessage).toBe('Signup failed');
    });
  });

  describe('template (integration)', () => {
    it('reveals store fields when accountType is owner', async () => {
      component.form.controls.accountType.setValue('owner');
      await fixture.whenStable();

      const compiled = fixture.nativeElement as HTMLElement;
      expect(compiled.querySelector('input[formcontrolname="storeName"]')).toBeTruthy();
    });

    it('hides store fields when accountType is customer', async () => {
      await fixture.whenStable();
      const compiled = fixture.nativeElement as HTMLElement;
      expect(compiled.querySelector('input[formcontrolname="storeName"]')).toBeFalsy();
    });

    it('disables the submit button when the form is invalid', async () => {
      component.form.controls.email.setValue('');
      await fixture.whenStable();

      const button: HTMLButtonElement =
        fixture.nativeElement.querySelector('button[type="submit"]');
      expect(button.disabled).toBeTrue();
    });
  });
});
