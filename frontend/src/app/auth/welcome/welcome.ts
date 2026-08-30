import { Component, inject } from '@angular/core';
import { Router } from '@angular/router';
import Keycloak from 'keycloak-js';
import { MatButton } from '@angular/material/button';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { LogoText } from '@shared/components/logo-text/logo-text';
import { Divider } from '../component/divider';

@Component({
  selector: 'app-welcome',
  imports: [LogoText, FormsModule, MatButton, Divider],
  host: {
    class: 'min-h-screen flex flex-col bg-primary',
  },
  template: `
    <div class="flex items-center justify-center h-16 bg-white border border-[#F0EEF0] ">
      <app-logo-text
        [width]="145.94"
        [height]="34.85"
        (click)="router.navigateByUrl('/products')"
        class="cursor-pointer"
      />
    </div>
    <div class="flex items-center justify-center grow px-4">
      <div class="flex flex-col max-w-[418px] min-w-[300px] -mt-[200px] gap-4">
        <div class="p-6 bg-white/50 rounded-2xl border border-[#F8F7F8]">
          <div class="flex flex-col gap-4">
            <button
              matButton="filled"
              class="btn-sign w-full"
              (click)="login()"
              data-cy="login-btn"
            >
              <span class="text-black"> Login</span>
            </button>
            <app-divider />
            <button
              matButton="filled"
              class="btn-sign w-full"
              (click)="goToSignup()"
              data-cy="create-account-btn"
            >
              <span class="text-black"> Create an Account</span>
            </button>
          </div>
        </div>
      </div>
    </div>
  `,
})
export class Welcome {
  private readonly keycloak = inject(Keycloak);
  readonly router = inject(Router);
  private readonly route = inject(ActivatedRoute);

  login(): void {
    const redirectUrl = this.route.snapshot.queryParamMap.get('redirectUrl') ?? '/'; // In case the use came from isAuthenticated guard redirection
    this.keycloak.login({
      redirectUri: window.location.origin + redirectUrl,
    });
  }

  goToSignup(): void {
    this.router.navigate(['/signup']);
  }
}
