import { Component, ViewEncapsulation, input } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { RouterLink } from '@angular/router';
import {keycloak} from '@core/keycloak';

@Component({
  selector: 'error-code',
  template: `
    <div class="matero-error-wrap">
      <div class="matero-error-code">{{ code() }}</div>
      @if (title()) {
        <div class="matero-error-title">{{ title() }}</div>
      }
      @if (message()) {
        <div class="matero-error-message">{{ message() }}</div>
      }
      @if (buttonType() == 'logout') {
        <div>
          <a matButton="elevated" color="primary" (click)="logout()" >Logout</a>
        </div>
      } @else {
        <div>
          <a matButton="elevated" color="primary" routerLink="/">{{buttonText()}}</a>
        </div>
      }

    </div>`,
  styleUrl: './error-code.scss',
  encapsulation: ViewEncapsulation.None,
  imports: [RouterLink, MatButtonModule],
})
export class ErrorCode {
  readonly code = input('');
  readonly title = input('');
  readonly message = input('');
  readonly buttonText = input('Back to Home');
  readonly buttonType = input<'logout' | 'normal'>('normal');


  logout() {
    keycloak.logout().then(()=> keycloak.login({ redirectUri:'/'}));
  }
}
