import { AfterViewInit, Component, inject, input, signal, ViewChild } from '@angular/core';
import { MatDivider, MatListItem, MatNavList } from '@angular/material/list';
import { MatSidenav, MatSidenavContainer, MatSidenavContent } from '@angular/material/sidenav';
import { TitleCasePipe } from '@angular/common';
import { ProductStore } from '../../product-store';
import { RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';
import { SidenavService } from '../../services/Sidenav';
import { Header } from '../header/header';
import {
  MatExpansionPanel,
  MatExpansionPanelHeader,
  MatExpansionPanelTitle,
} from '@angular/material/expansion';
import { MatCheckbox } from '@angular/material/checkbox';
import { MatButton, MatIconButton } from '@angular/material/button';
import { MatDialogClose } from '@angular/material/dialog';
import { CartSidenav } from '../../services/cart-sidenav';
import { CartItem } from '@components/molecules/cart-item/cart-item';
import { MatIcon } from '@angular/material/icon';
import { BreakpointObserver } from '@angular/cdk/layout';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { Cart } from '@components/cart/cart';

@Component({
  selector: 'app-sidenav-container',
  imports: [
    MatListItem,
    MatNavList,
    MatSidenav,
    MatSidenavContainer,
    MatSidenavContent,
    RouterOutlet,
    RouterLink,
    Header,
    MatExpansionPanel,
    MatExpansionPanelTitle,
    MatExpansionPanelHeader,
    MatDivider,
    MatCheckbox,
    MatButton,
    MatDialogClose,
    CartItem,
    MatIconButton,
    MatIcon,
    Cart,
  ],
  template: `
    <app-header class="relative z-10" />
    <div class="h-[calc(100%-64px)] overflow-auto">
      <mat-sidenav-container class="h-full">
        <mat-sidenav #sidenav mode="side">
          <div class="p-6">
            <h2 class="text-lg text-gray-900">Categories</h2>
            <mat-nav-list>
              @for (category of categories; track category.label) {
                <mat-expansion-panel class="nav-list">
                  <mat-expansion-panel-header>
                    <mat-panel-title>{{ category.label }}</mat-panel-title>
                  </mat-expansion-panel-header>

                  @for (sub of category.subcategories; track sub) {
                    <a mat-list-item [routerLink]="['/products']" [queryParams]="{ category: sub }">
                      {{ sub }}
                    </a>
                  }
                </mat-expansion-panel>
              }
            </mat-nav-list>
            <mat-divider></mat-divider>
            <div class="mt-3">
              <span class="font-semibold text-lg">Filters</span>
              <div class="flex flex-col gap-3">
                <div class="flex flex-col">
                  <span class="p-3 pb-2 font-normal text-[#71717A] text-base"
                    >Filter products by category</span
                  >
                  <mat-checkbox>Cat1</mat-checkbox>
                  <mat-checkbox>Cat2</mat-checkbox>
                  <mat-divider></mat-divider>
                </div>
                <div class="flex flex-col">
                  <span class="p-3 pb-2 font-normal text-[#71717A] text-base"
                    >Filter products by category</span
                  >
                  <mat-checkbox>Cat1</mat-checkbox>
                  <mat-checkbox>Cat2</mat-checkbox>
                </div>
              </div>
              <button matButton [mat-dialog-close]="true" cdkFocusInitial>Apply</button>
              <button matButton mat-dialog-close>Reset</button>
            </div>
          </div>
        </mat-sidenav>

        <mat-sidenav #cartSidenav position="end" class="min-w-[375px] md:min-w-[600px]">
          <app-cart />
        </mat-sidenav>

        <mat-sidenav-content>
          <router-outlet />
        </mat-sidenav-content>
      </mat-sidenav-container>
    </div>
  `,
  styles: ``,
})
export default class Layout implements AfterViewInit {
  store = inject(ProductStore);
  sidenavService = inject(SidenavService);
  cartSidenavService = inject(CartSidenav);

  @ViewChild('sidenav') sidenav!: MatSidenav;
  @ViewChild('cartSidenav') cartSidenav!: MatSidenav;

  categories = [
    { label: 'Dairy', subcategories: ['Milk', 'Cheese', 'Butter'] },
    { label: 'Fresh', subcategories: ['Fruits', 'Vegetables'] },
    { label: 'Frozen', subcategories: ['Ice Cream', 'Frozen Meals'] },
  ];

  isMobile = signal(false);

  constructor(bpo: BreakpointObserver) {
    bpo
      .observe('(max-width: 768px)')
      .pipe(takeUntilDestroyed())
      .subscribe((res) => this.isMobile.set(res.matches));
  }

  ngAfterViewInit() {
    this.sidenavService.setSidenav(this.sidenav);
    this.cartSidenavService.setSidenav(this.cartSidenav);
    // To remove the flash when rendering
    this.sidenav.close();
    this.cartSidenav.open();
  }
}
