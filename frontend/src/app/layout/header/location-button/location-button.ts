import { Component, inject } from '@angular/core';
import { MatButton } from '@angular/material/button';
import { MatIcon } from '@angular/material/icon';
import { MatDialog } from '@angular/material/dialog';
import { DeliveryAddressDialog } from '@components/molecules/delivery-address-dialog/delivery-address-dialog';

@Component({
  selector: 'app-location-button',
  imports: [MatButton, MatIcon],
  template: `
    <button matButton="outlined" class="btn-sm" (click)="editDeliveryAddress()">
      12, Rennes
      <mat-icon>location_on</mat-icon>
    </button>
  `,
  styles: ``,
})
export class LocationButton {
  dialog = inject(MatDialog);

  editDeliveryAddress() {
    this.dialog.open(DeliveryAddressDialog);
  }
}
