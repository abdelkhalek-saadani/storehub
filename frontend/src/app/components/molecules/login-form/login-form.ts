import { Component, signal } from '@angular/core';
import { Divider } from '../../atoms/divider/divider';
import { FormsModule } from '@angular/forms';
import { MatButton, MatIconButton } from '@angular/material/button';
import { MatInput, MatLabel } from '@angular/material/input';
import { MatTab, MatTabGroup } from '@angular/material/tabs';
import { MatFormField } from '@angular/material/form-field';
import { MatIcon } from '@angular/material/icon';
import { MatSuffix } from '@angular/material/form-field';
import { PhoneInput } from '../../../checkout/phone-input/phone-input';

@Component({
  selector: 'app-login-form',
  imports: [
    Divider,
    FormsModule,
    MatButton,
    MatFormField,
    MatIcon,
    MatIconButton,
    MatInput,
    MatLabel,
    MatSuffix,
    MatTab,
    MatTabGroup,
    PhoneInput,
  ],
  template: `
    <div class="flex flex-col max-w-[418px] gap-4">
      <div class="p-6 bg-white rounded-2xl border border-[#F8F7F8]">
        <mat-tab-group dynamicHeight [selectedIndex]="0">
          <mat-tab label="Email">
            <form>
              <mat-form-field class="mt-6">
                <mat-label>Email</mat-label>
                <input matInput placeholder="Enter your email" />
              </mat-form-field>

              <mat-form-field class="mt-6">
                <mat-label>Password</mat-label>
                <input
                  [type]="isVisible() ? 'text' : 'password'"
                  matInput
                  placeholder="Enter your password"
                />
                <button matSuffix matIconButton class="mr-2" (click)="toggleVisibility()">
                  <mat-icon [fontIcon]="isVisible() ? 'visibility_off' : 'visibility'"></mat-icon>
                </button>
              </mat-form-field>
              <button matButton="filled" class="w-full mt-8" disabled>
                Continue
                <mat-icon iconPositionEnd>arrow_forward</mat-icon>
              </button>
            </form>
          </mat-tab>
          <mat-tab label="Phone">
            <form>
              <app-phone-input class="w-full block mt-6" />

              <button matButton="filled" class="w-full mt-8">
                Send Code
                <mat-icon iconPositionEnd>arrow_forward</mat-icon>
              </button>
            </form>
          </mat-tab>
        </mat-tab-group>
      </div>
      <app-divider />
      <div class="flex flex-col gap-4">
        <button matButton="filled" class="btn-sign w-full">
          <span class="text-black"> Sign in with Google</span>
          <mat-icon svgIcon="google" />
        </button>
        <button matButton="filled" class="btn-sign w-full">
          <span class="text-black"> Sign in with meta</span>
          <mat-icon svgIcon="meta" />
        </button>
      </div>
    </div>
  `,
  styles: ``,
})
export class LoginForm {
  isVisible = signal(false);

  toggleVisibility(): void {
    this.isVisible.set(!this.isVisible());
  }
}
