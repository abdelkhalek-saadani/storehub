import {Component, signal} from '@angular/core';
import {FormsModule} from '@angular/forms';
import {MatButton} from '@angular/material/button';

import {MatInput, MatLabel} from '@angular/material/input';
import {MatButtonToggle, MatButtonToggleGroup} from '@angular/material/button-toggle';
import {MatIcon} from '@angular/material/icon';
import {BreakpointObserver} from '@angular/cdk/layout';
import {takeUntilDestroyed} from '@angular/core/rxjs-interop';

import {MatFormFieldModule} from '@angular/material/form-field';

@Component({
  selector: 'app-delivery-address-form',
  imports: [
    FormsModule,
    MatButton,
    MatInput,
    MatLabel,
    MatButtonToggleGroup,
    MatButtonToggle,
    MatIcon,
    MatFormFieldModule
  ],
  template: `
    <div class="gap-6 p-6 bg-white rounded-b-xl flex flex-col max-w-2xl">
      <div class="w-full max-w-[630px] max-h-[420px]">
        <img src="map-placeholder.jpg" class="cover" alt="Map Placeholder"/>
      </div>
      <form class="w-full flex flex-col gap-6">

        <div>
          <h3 class="text-[#1a1b1f] font-medium mb-2 text-base">
            Select Address Type
          </h3>
          <mat-button-toggle-group>
            <mat-button-toggle value="home">
              <mat-icon>home</mat-icon>
              Home
            </mat-button-toggle>
            <mat-button-toggle value="apartment">
              <mat-icon>apartment</mat-icon>
              @if (isXXSMobile()) {
                Apt.
              } @else {
                Apartment
              }
            </mat-button-toggle>
            <mat-button-toggle value="office">
              <mat-icon>work</mat-icon>
              Office
            </mat-button-toggle>
          </mat-button-toggle-group>
        </div>
        <mat-form-field>
          <mat-label>Street Address</mat-label>
          <input matInput>
        </mat-form-field>

        <div class="flex flex-col gap-6 md:flex-row md:gap-3">
          <mat-form-field>
            <mat-label>City</mat-label>
            <input matInput>
          </mat-form-field>

          <mat-form-field>
            <mat-label>N.of Apartment</mat-label>
            <input matInput>
          </mat-form-field>

          <mat-form-field>
            <mat-label>Zip Code</mat-label>
            <input matInput type="number">
          </mat-form-field>
        </div>
        <mat-form-field>
          <mat-label>Delivery Instructions</mat-label>
          <input matInput>
        </mat-form-field>


      </form>
      <button matButton="filled" class="w-full btn-pill">
        Save Changes
        <mat-icon>location_on</mat-icon>
      </button>

    </div>
  `,
  styles: ``,
})
export class DeliveryAddressForm {
  isXXSMobile = signal(false);

  constructor(bpo: BreakpointObserver) {
    bpo
      .observe('(max-width: 435px)')
      .pipe(takeUntilDestroyed())
      .subscribe(result => this.isXXSMobile.set(result.matches));
  }
}
