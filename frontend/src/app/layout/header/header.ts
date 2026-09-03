import { Component, computed, inject, signal } from '@angular/core';
import { MatToolbar } from '@angular/material/toolbar';
import { MatIcon } from '@angular/material/icon';
import { MatButton, MatIconButton } from '@angular/material/button';
import { SidenavService } from '../sidenav.service';
import { BreakpointObserver, Breakpoints } from '@angular/cdk/layout';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { MatFormFieldModule } from '@angular/material/form-field';
import { SearchBar } from './search-bar/search-bar';
import { LogoText } from '@shared/components/logo-text/logo-text';
import { StoreButton } from './store-button/store-button';
import { MatMenu, MatMenuItem, MatMenuTrigger } from '@angular/material/menu';
import { MatDivider } from '@angular/material/divider';
import { Router, RouterLink } from '@angular/router';
import { CartSidenav } from '@shared/service/cart-sidenav';
import Keycloak from 'keycloak-js';
import { KEYCLOAK_EVENT_SIGNAL } from 'keycloak-angular';
import { CartStore } from '../../cart/cart-store';
import { MatBadge } from '@angular/material/badge';
import { DecimalPipe } from '@angular/common';

@Component({
  selector: 'app-header',
  imports: [
    MatIcon,
    MatIconButton,
    MatButton,
    MatFormFieldModule,
    SearchBar,
    LogoText,
    StoreButton,
    MatMenu,
    MatDivider,
    MatMenuTrigger,
    MatMenuItem,
    RouterLink,
    MatBadge,
    DecimalPipe,
  ],
  template: `
    @if (isMobile()) {
      <div class="flex flex-col items-center w-full pt-6 px-4 gap-2 pb-2">
        <div class="flex items-center justify-between w-full">
          <button matIconButton class="menu" (click)="sidenavService.toggle()">
            <mat-icon>menu</mat-icon>
          </button>

          <app-store-button />
          <button
            matIconButton
            class="background-primary"
            (click)="cartSidenavService.toggle()"
            [matBadge]="itemCount()"
            data-cy="cart-btn"
          >
            <mat-icon>shopping_cart</mat-icon>
          </button>
        </div>
        <div class="px-8 w-full">
          <app-search-bar />
        </div>
      </div>
    } @else {
      <div class="px-10 py-3">
        <div class="flex items-center justify-between gap-2">
          <div class="flex items-center gap-5">
            <div class="flex items-center gap-1">
              <button matIconButton class="menu" (click)="sidenavService.toggle()">
                <mat-icon>menu</mat-icon>
              </button>
              <app-logo-text [width]="134" [height]="32" />
            </div>
            <app-store-button />
          </div>

          <app-search-bar />

          <div class="flex gap-4 items-center">
            <button matButton="filled" class="btn-pill-sm" routerLink="/wishlist">
              <mat-icon>favorite</mat-icon>
              0 Products
            </button>
            <button
              matButton="filled"
              class="btn-pill-sm"
              (click)="cartSidenavService.toggle()"
              data-cy="cart-btn"
            >
              <div class="flex items-center gap-1">
                <mat-icon>shopping_cart</mat-icon>
                <span>{{ total() | number: '1.2-2' }} TND</span>
                <div
                  class="flex items-center justify-center rounded-full bg-primary w-[26px] aspect-square"
                >
                  <span class="text-white text-sm">{{ itemCount() }}</span>
                </div>
              </div>
            </button>

            @if (isAuthenticated()) {
              <button matIconButton [matMenuTriggerFor]="profileMenu">
                <img src="avatar.png" class="w-8 h-8 rounded-full object-cover" />
              </button>

              <mat-menu #profileMenu="matMenu">
                <button mat-menu-item>
                  <mat-icon>person</mat-icon>
                  Profile
                </button>
                <button mat-menu-item>
                  <mat-icon>settings</mat-icon>
                  Settings
                </button>
                <mat-divider />
                <button mat-menu-item class="text-red-500" (click)="keycloak.logout()">
                  <mat-icon class="text-red-500">logout</mat-icon>
                  Logout
                </button>
              </mat-menu>
            } @else {
              <button matButton="filled" class="btn-md" (click)="router.navigateByUrl('/welcome')">
                Login/Register
              </button>
            }
          </div>
        </div>
      </div>
    }
  `,
  styles: ``,
})
export class Header {
  cartStore = inject(CartStore);
  sidenavService = inject(SidenavService);
  cartSidenavService = inject(CartSidenav);
  readonly keycloak = inject(Keycloak);
  private readonly keycloakSignal = inject(KEYCLOAK_EVENT_SIGNAL);
  router = inject(Router);
  total = this.cartStore.finalTotal;
  itemCount = this.cartStore.itemCount;

  isAuthenticated = computed(() => {
    this.keycloakSignal(); // triggers recompute on any keycloak event
    return this.keycloak.authenticated ?? false;
  });

  isMobile = signal(false);

  constructor(bpo: BreakpointObserver) {
    bpo
      .observe([Breakpoints.XSmall, Breakpoints.Small])
      .pipe(takeUntilDestroyed())
      .subscribe((res) => this.isMobile.set(res.matches));
  }
}
