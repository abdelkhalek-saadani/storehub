import { Component } from '@angular/core';
import { LogoText } from '../../components/atoms/logo-text/logo-text';
import { LoginForm } from '../../components/molecules/login-form/login-form';

@Component({
  selector: 'app-login',
  imports: [LogoText, LoginForm],
  host: {
    class: 'min-h-screen flex flex-col bg-primary',
  },
  template: `
    <div class="flex items-center justify-center h-16 bg-white border border-[#F0EEF0] ">
      <app-logo-text [width]="145.94" [height]="34.85" />
    </div>
    <div class="flex items-center justify-center grow px-4">
      <app-login-form />
    </div>
  `,
})
export default class LoginPage {}
