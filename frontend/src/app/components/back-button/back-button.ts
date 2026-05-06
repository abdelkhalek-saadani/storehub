import {Component, input} from '@angular/core';
import {RouterLink} from '@angular/router';
import {MatButton} from '@angular/material/button';
import {MatIcon} from '@angular/material/icon';

@Component({
  selector: 'app-back-button',
  imports: [
    RouterLink,
    MatButton,
    MatIcon
  ],
  template: `
    <button matButton="text" [routerLink]="navigateTo() ?? '/products'"
            class="-ms-2 flex items-center gap-1">
      <mat-icon>arrow_back</mat-icon>
      {{ label() }}
    </button>
  `,
  styles: `
  :host {
    display: block;
  }`,
})
export class BackButton {
  label = input('Continue shopping');
  navigateTo = input<string>();
}
