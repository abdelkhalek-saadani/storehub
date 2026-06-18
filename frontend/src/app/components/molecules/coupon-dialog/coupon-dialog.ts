import { Component } from '@angular/core';
import { MatDialogClose, MatDialogContent } from '@angular/material/dialog';
import { MatIcon } from '@angular/material/icon';
import { MatButton, MatIconButton } from '@angular/material/button';
import { MatFormField } from '@angular/material/form-field';
import { MatInput, MatLabel } from '@angular/material/input';

@Component({
  selector: 'app-coupon-dialog',
  imports: [
    MatDialogContent,
    MatIcon,
    MatIconButton,
    MatDialogClose,
    MatFormField,
    MatInput,
    MatLabel,
    MatButton,
  ],
  template: `
    <mat-dialog-content>
      <div class="flex flex-col gap-4 md:w-100">
        <div class="flex items-center justify-between">
          <span class="font-semibold text-[22px]"> Coupon </span>
          <button matIconButton [mat-dialog-close]="false" class="btn-close">
            <mat-icon>close</mat-icon>
          </button>
        </div>

        <div class="relative">
          <mat-form-field class="rounded">
            <mat-label>Enter Coupon</mat-label>
            <input matInput />
          </mat-form-field>
          <button matButton="filled" class="btn-md !absolute z-10 top-1/2 right-2 -mt-[19px]">
            Apply
          </button>
        </div>
      </div>
    </mat-dialog-content>
  `,
  styles: ``,
})
export class CouponDialog {}
