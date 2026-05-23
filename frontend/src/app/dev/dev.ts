import { Component, signal } from '@angular/core';
import { MatButton, MatFabButton, MatIconButton, MatMiniFabButton } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { Logo } from '../components/atoms/logo/logo';
import { LogoText } from '../components/atoms/logo-text/logo-text';
import { MatInput } from '@angular/material/input';
import { MatFormField } from '@angular/material/form-field';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatButtonToggle, MatButtonToggleGroup } from '@angular/material/button-toggle';
import {
  MatDatepicker,
  MatDatepickerInput,
  MatDatepickerToggle,
} from '@angular/material/datepicker';
import {
  MatTimepicker,
  MatTimepickerInput,
  MatTimepickerToggle,
} from '@angular/material/timepicker';
import { OffersSection } from '../components/molecules/offers-section';
import { MatChipsModule } from '@angular/material/chips';
import { Divider } from '../components/atoms/divider/divider';
import { SigninButton } from '../components/atoms/signin-button/signin-button';
import { MatTabsModule } from '@angular/material/tabs';
import { PhoneInput } from '../components/atoms/phone-input/phone-input';
import { LoginForm } from '../components/molecules/login-form/login-form';
import { MatSelectModule } from '@angular/material/select';
import { PickStoreLocationForm } from '../components/molecules/pick-store-location-form/pick-store-location-form';
import { BreakpointObserver } from '@angular/cdk/layout';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { DeliveryAddressForm } from '../components/molecules/delivery-address-form/delivery-address-form';
import { CategorySquaredButton } from '../components/atoms/category-squared-button/category-squared-button';
import { CategoryBar } from '../components/molecules/category-bar/category-bar';
import { Breakpoints } from '@core/constants/breakpoints';
import { ProductCard } from '../components/molecules/product-card/product-card';
import { MatCardImage } from '@angular/material/card';
import { CartItem } from '../components/molecules/cart-item/cart-item';

@Component({
  selector: 'app-dev',
  imports: [
    MatChipsModule,
    MatButton,
    MatIconModule,
    MatTabsModule,
    CommonModule,
    FormsModule,
    Logo,
    LogoText,
    MatFabButton,
    MatFormField,
    MatInput,
    MatFormFieldModule,
    MatIconButton,
    MatButtonToggleGroup,
    MatButtonToggle,
    MatDatepicker,
    MatDatepickerToggle,
    MatDatepickerInput,
    MatTimepickerInput,
    MatTimepicker,
    MatTimepickerToggle,
    OffersSection,
    Divider,
    SigninButton,
    ReactiveFormsModule,
    LoginForm,
    MatSelectModule,
    PickStoreLocationForm,
    DeliveryAddressForm,
    CategorySquaredButton,
    CategoryBar,
    ProductCard,
    CartItem,
  ],
  template: `
    <div class="catalog p-6 font-sans">
      <section class="mb-10">
        <h2 class="section-title">Components</h2>
        <div class="rows">
          <span class="label">Cart Item</span>

          <app-cart-item />

          <span class="label">Product Card</span>

          <app-product-card />

          <span class="label">Category Bar</span>

          <app-category-bar />

          <span class="label">Edit Delivery Address</span>

          <app-delivery-address-form />

          <span class="label">Pick Store Location</span>

          <app-pick-store-location-form />

          <span class="label">The Login Form</span>

          <app-login-form />
          <span class="label">Chips + category-chip class</span>
          <mat-chip-set class="category-chip" aria-label="Fish selection">
            <mat-chip>
              <img
                matChipAvatar
                src="https://material.angular.dev/assets/img/examples/shiba1.jpg"
                alt="Photo of a Shiba Inu"
              />Bread
            </mat-chip>
            <mat-chip>Two fish</mat-chip>
            <mat-chip>Three fish</mat-chip>
            <mat-chip disabled>Four fish</mat-chip>
          </mat-chip-set>
          <span class="label">OffersSection</span>
          <app-offers-section />
        </div>
      </section>
      <!-- ── INPUTS ── -->
      <section class="mb-10">
        <h2 class="section-title">Inputs</h2>
        <div class="rows">
          <div class="row">
            <span class="label">mat-form-field + mat-datepicker + mat-timepicker</span>
            <span class="variant">default</span>
            <div class="preview">
              <div class="flex gap-1 max-w-xs">
                <mat-form-field>
                  <mat-label>Delivery Date</mat-label>
                  <input matInput [matDatepicker]="datepicker" [(ngModel)]="value" />
                  <mat-datepicker #datepicker />
                  <mat-datepicker-toggle [for]="datepicker" matSuffix />
                </mat-form-field>

                <mat-form-field>
                  <mat-label>Time Slot</mat-label>
                  <input
                    matInput
                    [matTimepicker]="timepicker"
                    [(ngModel)]="value"
                    [ngModelOptions]="{ updateOn: 'blur' }"
                  />
                  <mat-timepicker #timepicker />
                  <mat-timepicker-toggle [for]="timepicker" matSuffix />
                </mat-form-field>
              </div>
              <br />
              <p>Value: {{ value }}</p>
            </div>
          </div>
          <div class="row">
            <span class="label">mat-form-field</span>
            <span class="variant">default</span>
            <div class="preview">
              <mat-form-field>
                <mat-label>Input</mat-label>
                <input matInput />
              </mat-form-field>
            </div>
          </div>
          <div class="row">
            <span class="label">mat-form-field</span>
            <span class="variant">rounded</span>
            <div class="preview">
              <mat-form-field class="rounded">
                <mat-label>Enter Coupon</mat-label>
                <input matInput />
              </mat-form-field>
            </div>
          </div>
          <div class="row">
            <span class="label">mat-form-field + search</span>
            <span class="variant">search</span>
            <div class="preview">
              <mat-form-field appearance="outline" class="search">
                <mat-label>Search By</mat-label>
                <input matInput placeholder="Placeholder" />
                <mat-icon class="ms-2" matPrefix>search</mat-icon>
              </mat-form-field>
            </div>
          </div>
          <div class="row">
            <span class="label">mat-button-toggle-group</span>
            <span class="variant">default</span>
            <div class="preview">
              <mat-button-toggle-group>
                <mat-button-toggle value="home">
                  <mat-icon>home</mat-icon>
                  Home
                </mat-button-toggle>
                <mat-button-toggle value="apartment">
                  <mat-icon>apartment</mat-icon>
                  Apartment
                </mat-button-toggle>
                <mat-button-toggle value="office">
                  <mat-icon>work</mat-icon>
                  Office
                </mat-button-toggle>
              </mat-button-toggle-group>
            </div>
          </div>
        </div>
      </section>

      <section class="mb-10">
        <h2 class="section-title">Divider</h2>
        <app-divider />
      </section>
      <!-- ── LOGOS ── -->
      <section class="mb-10">
        <h2 class="section-title">Logos</h2>
        <div class="rows">
          <div class="row">
            <span class="label">app-logo</span>
            <span class="variant">icon only</span>
            <div class="preview">
              <app-logo />
            </div>
          </div>
          <div class="row">
            <span class="label">app-logo-text</span>
            <span class="variant">logo + wordmark</span>
            <div class="preview">
              <app-logo-text />
            </div>
          </div>
        </div>
      </section>

      <!-- Text Button -->
      <section class="mb-10">
        <h2 class="section-title">Buttons — matButton="filled"</h2>
        <div class="rows">
          <span class="label">Category Squared Button</span>
          <app-category-squared-button />

          <div class="row">
            <span class="label">matButton + text</span>
            <span class="variant">default</span>
            <div class="preview">
              <button matButton="text">
                <mat-icon>picture_as_pdf</mat-icon>
                Invoice.pdf
              </button>
            </div>
          </div>
          <div class="row">
            <span class="label">matButton + text + danger</span>
            <span class="variant">default</span>
            <div class="preview">
              <button matButton="text" class="danger">
                <mat-icon>remove</mat-icon>
                Remove Coupon
              </button>
            </div>
          </div>
        </div>
      </section>
      <!-- ── ICON BUTTONS ── -->
      <section class="mb-10">
        <h2 class="section-title">Buttons — matIconButton</h2>
        <div class="rows">
          <div class="row">
            <span class="label">matIconButton</span>
            <span class="variant">counter</span>
            <div class="preview">
              <button matIconButton>
                <mat-icon>remove</mat-icon>
              </button>
              <span>1</span>
              <button matIconButton>
                <mat-icon>add</mat-icon>
              </button>
            </div>
          </div>
        </div>
      </section>

      <!-- ── FILLED BUTTONS ── -->
      <section class="mb-10">
        <h2 class="section-title">Buttons — matButton="filled"</h2>
        <div class="rows">
          <div class="row">
            <span class="label">filled + btn-pill + w-full</span>
            <span class="variant">checkout / wide</span>
            <div class="preview">
              <button matButton="filled" class="w-full btn-pill space-between">
                <span style="display:flex; align-items:center; gap:8px;">
                  <mat-icon>payment</mat-icon>
                  Checkout
                </span>
                <span>$120.00</span>
              </button>
            </div>
          </div>

          <div class="row">
            <span class="label">filled + btn-pill-xs</span>
            <span class="variant">xs pill</span>
            <div class="preview">
              <button matButton="filled" class="btn-pill-xs">
                <mat-icon>add_2</mat-icon>
                Add to cart
              </button>
            </div>
          </div>

          <div class="row">
            <span class="label">filled + btn-pill-sm</span>
            <span class="variant">sm pill / cart badge</span>
            <div class="preview">
              <button matButton="filled" class="btn-pill-sm">
                <mat-icon>shopping_cart</mat-icon>
                <span>15 TND</span>
                <span class="text-white bg-primary rounded-full p-1 text-sm ms-1">12</span>
              </button>
            </div>
          </div>

          <div class="row">
            <span class="label">filled + btn-pill-sm</span>
            <span class="variant">sm pill / favorites</span>
            <div class="preview">
              <button matButton="filled" class="btn-pill-sm">
                <mat-icon>favorite</mat-icon>
                3 Products
              </button>
            </div>
          </div>

          <div class="row">
            <span class="label">filled + danger</span>
            <span class="variant">destructive</span>
            <div class="preview">
              <button matButton="filled" class="danger">Cancel Order</button>
            </div>
          </div>

          <div class="row">
            <span class="label">filled + btn-sign</span>
            <span class="variant">OAuth / Google</span>
            <div class="preview">
              <app-signin-button type="google" />
            </div>
          </div>

          <div class="row">
            <span class="label">filled + btn-sign</span>
            <span class="variant">OAuth / Meta</span>
            <div class="preview">
              <app-signin-button type="meta" />
            </div>
          </div>

          <div class="row">
            <span class="label">filled + btn-pill</span>
            <span class="variant">pill / save</span>
            <div class="preview">
              <button matButton="filled" class="btn-pill">
                Save Changes
                <mat-icon>location_on</mat-icon>
              </button>
            </div>
          </div>

          <div class="row">
            <span class="label">filled + btn-pill</span>
            <span class="variant">pill / add to cart</span>
            <div class="preview">
              <button matButton="filled" class="btn-pill">
                Add to Cart
                <mat-icon>add_shopping_cart</mat-icon>
              </button>
            </div>
          </div>

          <div class="row">
            <span class="label">filled + btn-lg</span>
            <span class="variant">lg</span>
            <div class="preview">
              <button matButton="filled" class="btn-lg">
                Continue Checkout
                <mat-icon iconPositionEnd>arrow_forward</mat-icon>
              </button>
            </div>
          </div>

          <div class="row">
            <span class="label">filled + btn-icon</span>
            <span class="variant">icon end</span>
            <div class="preview">
              <button matButton="filled" class="btn-icon">
                Download
                <mat-icon iconPositionEnd>download</mat-icon>
              </button>
            </div>
          </div>

          <div class="row">
            <span class="label">filled + btn-sm</span>
            <span class="variant">sm</span>
            <div class="preview">
              <button matButton="filled" class="btn-sm">
                View All
                <mat-icon iconPositionEnd>arrow_forward</mat-icon>
              </button>
            </div>
          </div>

          <div class="row">
            <span class="label">filled + btn-md</span>
            <span class="variant">md</span>
            <div class="preview">
              <button matButton="filled" class="btn-md">
                View All
                <mat-icon iconPositionEnd>arrow_forward</mat-icon>
              </button>
            </div>
          </div>

          <div class="row">
            <span class="label">filled</span>
            <span class="variant">default</span>
            <div class="preview">
              <button matButton="filled">
                Continue
                <mat-icon iconPositionEnd>arrow_forward</mat-icon>
              </button>
            </div>
          </div>

          <div class="row">
            <span class="label">filled (disabled)</span>
            <span class="variant">disabled state</span>
            <div class="preview">
              <button matButton="filled" disabled>
                Continue
                <mat-icon iconPositionEnd>arrow_forward</mat-icon>
              </button>
            </div>
          </div>
        </div>
      </section>

      <!-- ── OUTLINED BUTTONS ── -->
      <section class="mb-10">
        <h2 class="section-title">Buttons — matButton="outlined"</h2>
        <div class="rows">
          <div class="row">
            <span class="label">outlined + btn-sm</span>
            <span class="variant">sm / location</span>
            <div class="preview">
              <button matButton="outlined" class="btn-sm">
                12, Rennes
                <mat-icon>location_on</mat-icon>
              </button>
            </div>
          </div>

          <div class="row">
            <span class="label">outlined</span>
            <span class="variant">default / nav</span>
            <div class="preview">
              <button matButton="outlined">
                Go back home
                <mat-icon iconPositionEnd>arrow_forward</mat-icon>
              </button>
            </div>
          </div>

          <div class="row">
            <span class="label">outlined (duplicate)</span>
            <span class="variant">default / nav</span>
            <div class="preview">
              <button matButton="outlined">
                Go back home
                <mat-icon iconPositionEnd>arrow_forward</mat-icon>
              </button>
            </div>
          </div>

          <div class="row">
            <span class="label">outlined</span>
            <span class="variant">default label</span>
            <div class="preview">
              <button matButton="outlined">Outlined button</button>
            </div>
          </div>
        </div>
      </section>

      <!-- ── ELEVATED BUTTONS ── -->
      <section class="mb-10">
        <h2 class="section-title">Buttons — matButton="elevated"</h2>
        <div class="rows">
          <div class="row">
            <span class="label">elevated + w-full</span>
            <span class="variant">full width</span>
            <div class="preview">
              <button matButton="elevated" class="w-full">
                Continue Checkout
                <mat-icon iconPositionEnd>arrow_forward</mat-icon>
              </button>
            </div>
          </div>
        </div>
      </section>

      <!-- ── FAB ── -->
      <section class="mb-10">
        <h2 class="section-title">Buttons — matFab extended</h2>
        <div class="rows">
          <div class="row">
            <span class="label">matFab extended</span>
            <span class="variant">toggle / active</span>
            <div class="preview">
              <button
                matFab
                extended
                [class.active]="activeTab() === 'phone'"
                (click)="toggleActiveTab()"
              >
                <mat-icon>phone</mat-icon>
                Phone
              </button>
            </div>
          </div>
        </div>
      </section>
    </div>
  `,
  styles: `
    .section-title {
      font-size: 11px;
      font-weight: 500;
      letter-spacing: 0.08em;
      text-transform: uppercase;
      color: var(--mat-sys-on-surface-variant);
      border-bottom: 1px solid var(--mat-sys-outline-variant);
      padding-bottom: 8px;
      margin-bottom: 12px;
    }

    .rows {
      display: flex;
      flex-direction: column;
      gap: 2px;
    }

    .row {
      display: grid;
      grid-template-columns: 200px 160px 1fr;
      align-items: center;
      gap: 12px;
      padding: 10px 12px;
      border-radius: 8px;
      transition: background 0.15s;

      &:hover {
        background: var(--mat-sys-surface-container);
      }
    }

    .label {
      font-size: 12px;
      font-family: monospace;
      color: var(--mat-sys-on-surface-variant);
    }

    .variant {
      font-size: 11px;
      color: var(--mat-sys-outline);
    }

    .preview {
      display: flex;
      align-items: center;
      gap: 8px;
      flex-wrap: wrap;
    }
  `,
})
export default class Dev {
  value!: Date;

  isMdDevice = signal(false);
  quantity = signal(5);

  constructor(bpo: BreakpointObserver) {
    bpo
      .observe(Breakpoints.md)
      .pipe(takeUntilDestroyed())
      .subscribe((result) => this.isMdDevice.set(result.matches));
  }

  activeTab = signal('email');

  toggleActiveTab() {
    if (this.activeTab() === 'phone') {
      this.activeTab.update((at) => '');
      return;
    }
    this.activeTab.update((at) => 'phone');
  }
}
