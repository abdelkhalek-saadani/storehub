import {Component, inject, signal} from '@angular/core';
import {MatButton, MatIconButton} from '@angular/material/button';
import {
  MAT_DIALOG_DATA, MatDialog,
  MatDialogClose,
  MatDialogContainer,
  MatDialogContent,
  MatDialogRef
} from '@angular/material/dialog';
import {MatIcon} from '@angular/material/icon';
import {MatFormField, MatPrefix, MatSuffix} from '@angular/material/form-field';
import {MatInput} from '@angular/material/input';
import {MatLabel} from '@angular/material/form-field';
import {
  FormBuilder,
  FormControl,
  FormGroup,
  NonNullableFormBuilder,
  ReactiveFormsModule,
  Validators
} from '@angular/forms';
import {A11yModule} from '@angular/cdk/a11y';
import {ProductStore} from '../../product-store';
import {Router} from '@angular/router';
import {SignUpDialog} from '../sign-up-dialog/sign-up-dialog';
import {AuthFacade} from '../../services/AuthFacade';
import {SignInParams} from '../../models/User';

@Component({
  selector: 'app-sign-in-dialog',
  imports: [
    MatIconButton,
    MatDialogClose,
    MatIcon,
    MatFormField,
    MatInput,
    ReactiveFormsModule,
    MatPrefix,
    MatSuffix,
    MatButton,
    A11yModule,
    MatDialogContent,

  ],
  template: `
    <mat-dialog-content class="p-8 max-w-[400px] flex flex-col">
      <div class="flex justify-between">
        <div><h2 class="text-xl font-medium mb-1">Sign In</h2>
          <p class="text-sm text-gray-500">Sign in to your account to continue shopping</p></div>
        <button matIconButton matDialogClose class="-mt-2 -mr-2">
          <mat-icon>
            close
          </mat-icon>
        </button>
      </div>
      <form [formGroup]="signInForm" (ngSubmit)="submit()" class="mt-6">
        <mat-form-field class="w-full mb-4 ">
          <mat-icon matPrefix>email</mat-icon>
          <input matInput formControlName="email" type="email" placeholder="Enter your email" cdkFocusInitial>
        </mat-form-field>

        <mat-form-field class="w-full mb-6 ">
          <mat-icon matPrefix>lock</mat-icon>
          <input
            matInput
            formControlName="password"
            [type]="isVisible() ? 'text': 'password'"
            placeholder="Enter your password">
          <button
            matSuffix
            matIconButton
            class="mr-2"
            (click)="toggleVisibility()">
            <mat-icon [fontIcon]="isVisible()? 'visibility_off': 'visibility'"></mat-icon>
          </button>
        </mat-form-field>
        <button
          type="submit"
          matButton="filled"
          class="w-full"
        >
          Sign In
        </button>
      </form>
      <p class="text-sm text-gray-500 mt-2 text-center">
        Don't have an account?
        <a class="text-blue-600 cursor-pointer" (click)="openSignUpDialog()">
          Sign Up
        </a>
      </p>
    </mat-dialog-content>
  `,
  styles: ``,
})
export class SignInDialog {
  formBuilder = inject(NonNullableFormBuilder);
  signInForm = this.formBuilder.group({
    email: ['abdelkhaleksaadani@gmail.com', [Validators.required, Validators.email]],
    password: ['a strong password You know', Validators.required]
  })
  data = inject<{ redirect: boolean, redirectTo: string }>(MAT_DIALOG_DATA);
  store = inject(ProductStore);
  dialogRef = inject(MatDialogRef);
  router = inject(Router);
  dialog = inject(MatDialog);
  authFacade = inject(AuthFacade);

  submit() {
    if (!this.signInForm.valid){
      this.signInForm.markAsTouched();
      return
    }
    const {email, password} = this.signInForm.value;
    // I used facade here because the sign in logic contains redirection
    // And to keep store pure from redirection
    this.authFacade.signIn({
      user: {
        email: email!,
        password: password!
      },
      redirect: this.data.redirect,
      redirectTo: this.data.redirectTo
    });
    this.dialogRef.close();
  }

  isVisible = signal(false);

  toggleVisibility() {
    this.isVisible.set(!this.isVisible());
  }

  openSignUpDialog() {
    this.dialogRef.close();
    this.dialog.open(SignUpDialog, {
      disableClose: true,
      data: this.data
    });
  }
}
