import {Component, inject} from '@angular/core';
import {MatButton, MatIconButton} from '@angular/material/button';
import {MatIcon} from '@angular/material/icon';
import {RouterLink} from '@angular/router';
import {ProductStore} from '../../product-store';
import {MatBadge} from '@angular/material/badge';
import {MatDialog} from '@angular/material/dialog';
import {SignInDialog} from '../../components/sign-in-dialog/sign-in-dialog';
import {SignUpDialog} from '../../components/sign-up-dialog/sign-up-dialog';
import {MatMenu, MatMenuItem, MatMenuTrigger} from '@angular/material/menu';
import {MatDivider} from '@angular/material/divider';

@Component({
  selector: 'app-header-actions',
  imports: [
    MatButton,
    MatIcon,
    MatIconButton,
    RouterLink,
    MatBadge,
    MatMenu,
    MatMenuItem,
    MatMenuTrigger,
    MatDivider
  ],
  template: `
    <div class="flex items-center gap-2">
      <button matIconButton routerLink="wishlist" [matBadge]="store.wishlistCounter()"
              [matBadgeHidden]="store.wishlistCounter()===0">
        <mat-icon>favorite</mat-icon>
      </button>
      <button matIconButton routerLink="cart" [matBadge]="store.cartItemsCounter()"
              [matBadgeHidden]="store.cartItemsCounter()===0">
        <mat-icon>shopping_cart</mat-icon>
      </button>
      @if (this.store.user(); as user) {
        <button matIconButton [matMenuTriggerFor]="menu">

          <!--            [src]="user.imageUrl"-->
          <img
            class="w-8 h-8 rounded-full"
            src="https://randomuser.me/api/portraits/men/1.jpg"
            alt="Abdelkhalek">
        </button>
        <mat-menu #menu="matMenu" xPosition="before">
          <button mat-menu-item>
            <div class="flex flex-col min-w-[200px]">
              <span class="text-sm font-medium">{{user.name}}</span>
              <span class="text-xs text-gray-500">{{ user.email }}</span>
            </div>
          </button>
          <mat-divider></mat-divider>
          <button mat-menu-item class="!min-h-[32px] my-44" (click)="signOut()" >
            <mat-icon>
              logout
            </mat-icon>
            <span> Sign Out </span>
          </button>
        </mat-menu>
      } @else {
        <button matButton="text" (click)="openSignInDialog()">
          Sign In
        </button>
        <button matButton="filled" (click)="openSignUpDialog()">
          Sign Up
        </button>
      }
    </div>
  `,
  styles: ``,
})
export class HeaderActions {
  store = inject(ProductStore);
  dialog = inject(MatDialog);

  openSignInDialog() {
    const dialogRef = this.dialog.open(SignInDialog, {
      data: {redirect: false}
    });
  }
  openSignUpDialog() {
    const dialogRef = this.dialog.open(SignUpDialog, {
      data: {redirect: false}
    });
  }

  signOut() {
    this.store.signOut();
  }
}
