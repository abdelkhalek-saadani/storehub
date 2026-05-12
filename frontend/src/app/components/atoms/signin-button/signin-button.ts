import { Component, input } from '@angular/core';
import {MatIconModule} from '@angular/material/icon';
import {MatButtonModule} from '@angular/material/button';

@Component({
  selector: 'app-signin-button',
  imports: [ MatIconModule, MatButtonModule],
  template: `
    @if (type()=='google') {
      <button matButton="filled" class="btn-sign w-full">
        <span class="text-black"> Sign In With Google</span>
        <mat-icon svgIcon="google"></mat-icon>
      </button>
    } @else if (type()=='meta') {
      <button matButton="filled" class="btn-sign w-full">
        Sign In With Meta
        <mat-icon svgIcon="meta"></mat-icon>
      </button>
    }
  `,
  styles: ``,
})
export class SigninButton {
  type = input.required<'meta' | 'google'>();
}
