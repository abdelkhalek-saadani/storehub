import {Component, inject, signal} from '@angular/core';
import {MatButton, MatFabButton, MatIconButton} from '@angular/material/button';
import {MatIconModule, MatIconRegistry} from '@angular/material/icon';
import {FormsModule} from '@angular/forms';
import {CommonModule} from '@angular/common';
import {Logo} from '../components/atoms/logo/logo';
import {LogoText} from '../components/atoms/logo-text/logo-text';
import {MatInput} from '@angular/material/input';
import {MatFormField} from '@angular/material/form-field';
import {MatInputModule} from '@angular/material/input';
import {MatFormFieldModule} from '@angular/material/form-field';
import {DomSanitizer} from '@angular/platform-browser';
import {MatBadge} from '@angular/material/badge';



@Component({
  selector: 'app-dev',
  imports: [
    MatButton,
    MatIconModule,
    CommonModule, FormsModule, Logo, LogoText, MatFabButton, MatFormField, MatInput, MatFormFieldModule, MatBadge, MatIconButton
  ],
  template: `
    <!-- dev.component.html -->
    <div class="catalog p-6 font-sans">

      <!-- ── INPUTS ── -->
      <section class="mb-10">
        <h2 class="section-title">Inputs</h2>
        <div class="rows">
          <div class="row">
            <span class="label">mat-form-field</span>
            <span class="variant">default</span>
            <div class="preview">
              <mat-form-field>
                <mat-label>Input</mat-label>
                <input matInput>
              </mat-form-field>
            </div>
          </div>
        </div>
      </section>

      <!-- ── LOGOS ── -->
      <section class="mb-10">
        <h2 class="section-title">Logos</h2>
        <div class="rows">
          <div class="row">
            <span class="label">app-logo</span>
            <span class="variant">icon only</span>
            <div class="preview">
              <app-logo/>
            </div>
          </div>
          <div class="row">
            <span class="label">app-logo-text</span>
            <span class="variant">logo + wordmark</span>
            <div class="preview">
              <app-logo-text/>
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
              <button matButton="filled" class="btn-sign">
                <span class="text-black"> Sign In With Google</span>
                <mat-icon svgIcon="google"></mat-icon>
              </button>
            </div>
          </div>

          <div class="row">
            <span class="label">filled + btn-sign</span>
            <span class="variant">OAuth / Meta</span>
            <div class="preview">
              <button matButton="filled" class="btn-sign">
                Sign In With Meta
                <mat-icon svgIcon="meta"></mat-icon>
              </button>
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
              <button matFab extended
                      [class.active]="activeTab() === 'phone'"
                      (click)="toggleActiveTab()">
                <mat-icon>phone</mat-icon>
                Phone
              </button>
            </div>
          </div>
        </div>
      </section>

    </div>
    <!--    <p class="bg-primary-blur">-->
    <!--      Components-->
    <!--    </p>-->


    <!--    <h1>Inputs</h1>-->
    <!--    <hr class="mb-2">-->
    <!--    <div class="flex flex-wrap gap-3 p-2">-->
    <!--      <mat-form-field>-->
    <!--        <mat-label>Input</mat-label>-->
    <!--        <input matInput>-->
    <!--      </mat-form-field>-->
    <!--    </div>-->


    <!--    <h1>Logos</h1>-->
    <!--    <hr class="mb-2">-->
    <!--    <div class="flex flex-wrap gap-3 p-2">-->
    <!--      <app-logo/>-->
    <!--      <app-logo-text/>-->
    <!--      <hr class="my-2">-->
    <!--    </div>-->

    <!--    <h1>Buttons</h1>-->
    <!--    <hr class="mb-2">-->
    <!--    <div class="flex flex-wrap gap-3 p-2">-->

    <!--      <div style="display:flex; align-items:center; gap:8px;">-->
    <!--        <button matIconButton>-->
    <!--          <mat-icon>remove</mat-icon>-->
    <!--        </button>-->
    <!--        <span>1</span>-->
    <!--        <button matIconButton>-->
    <!--          <mat-icon>add</mat-icon>-->
    <!--        </button>-->
    <!--      </div>-->

    <!--      <button matButton="filled" class="w-full btn-pill" >-->
    <!--        <div class="flex items-center">-->
    <!--          <span style="display:flex; align-items:center; gap:8px;">-->
    <!--            <mat-icon>payment</mat-icon>-->
    <!--            Checkout-->
    <!--          </span>-->
    <!--          <div class="w-20"></div>-->
    <!--          <span>$120.00</span>-->
    <!--        </div>-->
    <!--      </button>-->

    <!--      <button matButton="filled" class="btn-pill-xs">-->
    <!--        <mat-icon>add_2</mat-icon>-->
    <!--        Add to cart-->
    <!--      </button>-->

    <!--      <button matButton="filled" class="btn-pill-sm">-->
    <!--        <mat-icon>shopping_cart</mat-icon>-->
    <!--        <span>15 TND</span>-->
    <!--        <span class="text-white bg-primary rounded-full p-1 text-sm ms-1">12</span>-->
    <!--      </button>-->

    <!--      <button matButton="filled" class="btn-pill-sm">-->
    <!--        <mat-icon>favorite</mat-icon>-->
    <!--        3 Products-->
    <!--      </button>-->

    <!--      <button matButton="filled" class="danger">-->
    <!--        Cancel Order-->
    <!--      </button>-->

    <!--      <button matButton="outlined" class="btn-sm">-->
    <!--        12, Rennes-->
    <!--        <mat-icon>location_on</mat-icon>-->
    <!--      </button>-->
    <!--      <button matButton="outlined">-->
    <!--        Go back home-->
    <!--        <mat-icon iconPositionEnd>arrow_forward</mat-icon>-->
    <!--      </button>-->

    <!--      <button matButton="filled" class="btn-sign">-->
    <!--        <span class="text-black"> Sign In With Google</span>-->
    <!--        <mat-icon svgIcon="google"></mat-icon>-->
    <!--      </button>-->

    <!--      <button matButton="filled" class="btn-sign">-->
    <!--        Sign In With Meta-->
    <!--        <mat-icon svgIcon="meta"></mat-icon>-->
    <!--      </button>-->


    <!--      <button matButton="filled" class="btn-pill">-->
    <!--        Save Changes-->
    <!--        <mat-icon>location_on</mat-icon>-->
    <!--      </button>-->

    <!--      <button matButton="filled" class="btn-pill">-->
    <!--        Add to Cart-->
    <!--        <mat-icon>add_shopping_cart</mat-icon>-->
    <!--      </button>-->

    <!--      <button matButton="outlined">-->
    <!--        Go back home-->
    <!--        <mat-icon iconPositionEnd>arrow_forward</mat-icon>-->
    <!--      </button>-->

    <!--      <button matButton="filled" class="btn-lg">-->
    <!--        Continue Checkout-->
    <!--        <mat-icon iconPositionEnd>arrow_forward</mat-icon>-->
    <!--      </button>-->

    <!--      <button matButton="elevated" class="w-full">-->
    <!--        Continue Checkout-->
    <!--        <mat-icon iconPositionEnd>arrow_forward</mat-icon>-->
    <!--      </button>-->

    <!--      <button matButton="filled" class="btn-icon">-->
    <!--        Download-->
    <!--        <mat-icon iconPositionEnd>download</mat-icon>-->
    <!--      </button>-->

    <!--      <button matButton="filled" class="btn-sm">-->
    <!--        View All-->
    <!--        <mat-icon iconPositionEnd>arrow_forward</mat-icon>-->
    <!--      </button>-->

    <!--      <button matButton="filled" class="btn-md">-->
    <!--        View All-->
    <!--        <mat-icon iconPositionEnd>arrow_forward</mat-icon>-->
    <!--      </button>-->
    <!--      <button matButton="filled">-->
    <!--        Continue-->
    <!--        <mat-icon iconPositionEnd>arrow_forward</mat-icon>-->
    <!--      </button>-->

    <!--      <button matButton="filled" disabled>-->
    <!--        Continue-->
    <!--        <mat-icon iconPositionEnd>arrow_forward</mat-icon>-->
    <!--      </button>-->

    <!--      <br>-->
    <!--      <button matButton="outlined">Outlined button</button>-->
    <!--      <br>-->
    <!--      <button matFab extended-->
    <!--              [class.active]="activeTab() === 'phone'"-->

    <!--              (click)="toggleActiveTab()"-->
    <!--      >-->
    <!--        <mat-icon>phone</mat-icon>-->
    <!--        Phone-->
    <!--      </button>-->
    <!--      <hr class="my-2">-->
    <!--    </div>-->

  `,
  styles: `
    // dev.component.scss
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
    }`,
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

