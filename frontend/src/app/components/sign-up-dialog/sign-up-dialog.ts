import {Component, inject} from '@angular/core';
import {NonNullableFormBuilder, ReactiveFormsModule, Validators} from '@angular/forms';
import {MAT_DIALOG_DATA, MatDialog, MatDialogClose, MatDialogContent, MatDialogRef} from '@angular/material/dialog';
import {MatIcon} from '@angular/material/icon';
import {MatFormField, MatPrefix,} from '@angular/material/form-field';
import {MatInput} from '@angular/material/input';
import {MatButton, MatIconButton} from '@angular/material/button';
import {ProductStore} from '../../product-store';
import {Router} from '@angular/router';
import {SignInDialog} from '../sign-in-dialog/sign-in-dialog';

@Component({
  selector: 'app-sign-up-dialog',
  imports: [
    MatDialogContent,
    MatIcon,
    ReactiveFormsModule,
    MatFormField,
    MatInput,
    MatIconButton,
    MatDialogClose,
    MatButton,
    MatPrefix
  ],
  template: `
    <mat-dialog-content class="p-8 max-w-[400px] flex flex-col">
      <div class="flex justify-between">
        <div><h2 class="text-xl font-medium mb-1">Sign Up</h2>
          <p class="text-sm text-gray-500">Join us and start shopping today</p></div>
        <button matIconButton matDialogClose class="-mt-2 -mr-2">
          <mat-icon>
            close
          </mat-icon>
        </button>
      </div>
      <form [formGroup]="signUpForm" class="mt-6">
        <mat-form-field class="w-full mb-4">
          <mat-icon matPrefix>person</mat-icon>
          <input matInput formControlName="name" type="text" placeholder="Enter your name" cdkFocusInitial>
        </mat-form-field>
        <mat-form-field class="w-full mb-4 ">
          <mat-icon matPrefix>email</mat-icon>
          <input matInput formControlName="email" type="email" placeholder="Enter your email">
        </mat-form-field>
        <mat-form-field class="w-full mb-4 ">
          <mat-icon matPrefix>lock</mat-icon>
          <input matInput formControlName="password" type="password" placeholder="Enter your password">
        </mat-form-field>
        <mat-form-field class="w-full mb-6 ">
          <mat-icon matPrefix>lock</mat-icon>
          <input matInput formControlName="confirmPassword" type="password" placeholder="Confirm your password">
        </mat-form-field>
        <button type="submit" matButton="filled" class="w-full" (click)="submit()">
          Create Account
        </button>
      </form>
      <p class="text-sm text-gray-500 mt-2 text-center">
        Already have an account?
        <a class="text-blue-600 cursor-pointer" (click)="openSignInDialog()">
          Sign In
        </a>
      </p>
    </mat-dialog-content>
  `,
  styles: ``,
})
export class SignUpDialog {
  formBuilder = inject(NonNullableFormBuilder);
  signUpForm = this.formBuilder.group({
    name: ['Abdelkhalek', [Validators.required]],
    email: ['abdelkhaleksaadani@gmail.com', [Validators.required, Validators.email]],
    password: ['a strong password You know', Validators.required],
    confirmPassword: ['a strong password You know', Validators.required]
  });
  router = inject(Router);
  store = inject(ProductStore);
  data: { redirect: boolean, redirectTo: string } = inject(MAT_DIALOG_DATA);
  dialogRef = inject(MatDialogRef);
  dialog = inject(MatDialog);

  submit() {
    this.store.signUp({
      name: this.signUpForm.value.name!,
      email: this.signUpForm.value.email!,
      password: this.signUpForm.value.password!
    });
    if (this.data.redirect) this.router.navigate([this.data.redirectTo]);
    this.dialogRef.close();
  }

  openSignInDialog() {
    this.dialogRef.close();
    this.dialog.open(SignInDialog, {data: this.data});
  }
}
