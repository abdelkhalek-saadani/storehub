import {Component, signal} from '@angular/core';
import {MatButton, MatFabButton} from '@angular/material/button';
import {MatIconModule} from '@angular/material/icon';
import {FormsModule} from '@angular/forms';
import {CommonModule} from '@angular/common';
import {Logo} from '../components/atoms/logo/logo';
import {LogoText} from '../components/atoms/logo-text/logo-text';
import {MatInput} from '@angular/material/input';
import {MatFormField} from '@angular/material/form-field';
import {MatInputModule} from '@angular/material/input';
import {MatFormFieldModule} from '@angular/material/form-field';



@Component({
  selector: 'app-dev',
  imports: [
    MatButton,
    MatIconModule,
    CommonModule, FormsModule, Logo, LogoText, MatFabButton, MatFormField, MatInput, MatFormFieldModule
  ],
  template: `
    <p class="bg-primary-blur">
      Components
    </p>


    <h1>Inputs</h1>
    <hr class="mb-2">
    <div class="flex flex-wrap gap-3 p-2">
      <mat-form-field>
        <mat-label>Input</mat-label>
        <input matInput>
      </mat-form-field>
    </div>


    <h1>Logos</h1>
    <hr class="mb-2">
    <div class="flex flex-wrap gap-3 p-2">
      <app-logo/>
      <app-logo-text/>
      <hr class="my-2">
    </div>

    <h1>Buttons</h1>
    <hr class="mb-2">
    <div class="flex flex-wrap gap-3 p-2">


      <button matButton="filled" class="btn-lg">
        Continue Checkout
        <mat-icon iconPositionEnd>arrow_forward</mat-icon>
      </button>

      <button matButton="elevated" class="w-full">
        Continue Checkout
        <mat-icon iconPositionEnd>arrow_forward</mat-icon>
      </button>

      <button matButton="filled" class="btn-icon">
        Download
        <mat-icon iconPositionEnd>download</mat-icon>
      </button>

      <button matButton="filled" class="btn-sm">
        View All
        <mat-icon iconPositionEnd>arrow_forward</mat-icon>
      </button>

      <button matButton="filled" class="btn-md">
        View All
        <mat-icon iconPositionEnd>arrow_forward</mat-icon>
      </button>
      <button matButton="filled">
        Continue
        <mat-icon iconPositionEnd>arrow_forward</mat-icon>
      </button>

      <button matButton="filled" disabled>
        Continue
        <mat-icon iconPositionEnd>arrow_forward</mat-icon>
      </button>

      <br>
      <button matButton="outlined">Outlined button</button>
      <br>
      <button matFab extended
              [class.active]="activeTab() === 'phone'"

              (click)="toggleActiveTab()"
      >
        <mat-icon>phone</mat-icon>
        Phone
      </button>
      <hr class="my-2">
    </div>

  `,
  styles: ``,
})
export default class Dev {
  activeTab = signal("email");
  toggleActiveTab() {
    if (this.activeTab() === 'phone') {
      this.activeTab.update(at => '');
      return;
    }
    this.activeTab.update(at => 'phone');

  }
}

