import { Component } from '@angular/core';
import { MatButton } from '@angular/material/button';
import { MatIcon } from '@angular/material/icon';

@Component({
  selector: 'app-location-button',
  imports: [MatButton, MatIcon],
  template: `
    <button matButton="outlined" class="btn-sm">
      12, Rennes
      <mat-icon>location_on</mat-icon>
    </button>
  `,
  styles: ``,
})
export class LocationButton {}
