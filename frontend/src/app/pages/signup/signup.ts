import { Component, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { HttpClient } from '@angular/common/http';
import Keycloak from 'keycloak-js';
import { MatFormField, MatLabel } from '@angular/material/form-field';
import { MatInput } from '@angular/material/input';
import { MatButton } from '@angular/material/button';

@Component({
  selector: 'app-signup',
  standalone: true,
  imports: [ReactiveFormsModule, MatLabel, MatInput, MatFormField, MatButton],
  template: `
    <form [formGroup]="form" (ngSubmit)="submit()">
      <mat-form-field appearance="outline">
        <mat-label>First Name</mat-label>
        <input matInput formControlName="firstName" value="Abdelkhalek" />
      </mat-form-field>

      <mat-form-field appearance="outline">
        <mat-label>Last Name</mat-label>
        <input matInput formControlName="lastName" />
      </mat-form-field>

      <mat-form-field appearance="outline">
        <mat-label>Address</mat-label>
        <input matInput formControlName="address" />
      </mat-form-field>

      <mat-form-field appearance="outline">
        <mat-label>Phone Number</mat-label>
        <input matInput formControlName="phoneNumber" />
      </mat-form-field>

      <mat-form-field appearance="outline">
        <mat-label>Email</mat-label>
        <input matInput formControlName="email" placeholder="Email" />
      </mat-form-field>

      <mat-form-field appearance="outline">
        <mat-label>Password</mat-label>
        <input matInput formControlName="password" type="password" placeholder="Password" />
      </mat-form-field>

      <button
        mat-raised-button
        color="primary"
        type="submit"
        [disabled]="form.invalid || submitting()"
      >
        Sign up
      </button>
    </form>
  `,
})
export class Signup {
  private readonly fb = inject(FormBuilder);
  private readonly http = inject(HttpClient);
  private readonly keycloak = inject(Keycloak);

  submitting = signal(false);

  form = this.fb.group({
    firstName: ['Abdelkhalek', Validators.required],
    lastName: ['Saadani', Validators.required],
    address: ['cite Zayatine', Validators.required],
    phoneNumber: ['23000999', Validators.required],
    email: ['abdelkhaleksaadani@gmail.com', [Validators.required, Validators.email]],
    password: ['password', [Validators.required, Validators.minLength(8)]],
  });

  submit(): void {
    if (this.form.invalid) return;
    this.submitting.set(true);

    // Calls existing Java backend endpoint, which uses the
    // Keycloak Admin REST API server-side to create the user.
    this.http.post('http://localhost:8080/api/auth/signup', this.form.value).subscribe({
      next: () => {
        // Signup just creates the account, login still goes through Keycloak.
        this.keycloak.login({ redirectUri: window.location.origin + '/dev' });
      },
      error: (err) => {
        this.submitting.set(false);
        console.error('Signup failed', err);
      },
    });
  }
}
