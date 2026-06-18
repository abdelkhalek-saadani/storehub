import { Component } from '@angular/core';
import { MatDialogClose, MatDialogContent } from '@angular/material/dialog';
import { DeliveryAddressForm } from '@components/molecules/delivery-address-form/delivery-address-form';
import { MatIcon } from '@angular/material/icon';
import { MatIconButton } from '@angular/material/button';

@Component({
  selector: 'app-delivery-address-dialog',
  imports: [MatDialogContent, DeliveryAddressForm, MatIcon, MatIconButton, MatDialogClose],
  template: `
    <mat-dialog-content class="bg-white">
      <div class="flex items-center justify-between mb-4">
        <span class="font-semibold text-[22px]"> Edit Delivery Address </span>
        <button matIconButton [mat-dialog-close]="false" class="btn-close">
          <mat-icon>close</mat-icon>
        </button>
      </div>
      <app-delivery-address-form />
    </mat-dialog-content>
  `,
  styles: ``,
})
export class DeliveryAddressDialog {}
