import { Component } from '@angular/core';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {MatButton} from '@angular/material/button';
import {MatFormFieldModule} from '@angular/material/form-field';
import {MatIconModule} from '@angular/material/icon';
import {MatSelectModule} from '@angular/material/select';


@Component({
  selector: 'app-pick-store-location-form',
  imports: [
    FormsModule,
    MatButton,
    MatFormFieldModule,
    MatIconModule,
    MatSelectModule,
    ReactiveFormsModule
  ],
  template: `
    <div class="bg-white rounded-b-xl p-6 flex flex-col gap-6 max-w-2xl">
      <div class="w-full max-w-[630px] max-h-[420px]">
        <img src="map-placeholder.jpg" class="cover" alt="Map Placeholder"/>
      </div>
      <form class="w-full">
        <mat-form-field>
          <mat-label>Select Store Location</mat-label>
          <mat-select>
            <mat-option value="grombalia-store-id">Grombalia</mat-option>
            <mat-option value="ariana-store-id">Ariana</mat-option>
          </mat-select>
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
export class PickStoreLocationForm {

}
