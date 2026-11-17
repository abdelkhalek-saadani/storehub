import { AfterViewInit, Component, inject, signal, ViewChild } from '@angular/core';
import { MatDivider, MatListItem, MatNavList } from '@angular/material/list';
import { MatSidenav, MatSidenavContainer, MatSidenavContent } from '@angular/material/sidenav';
import { RouterLink, RouterOutlet } from '@angular/router';
import { SidenavService } from '../sidenav.service';
import { Header } from '../header/header';
import {
  MatExpansionPanel,
  MatExpansionPanelHeader,
  MatExpansionPanelTitle,
} from '@angular/material/expansion';
import { MatCheckbox } from '@angular/material/checkbox';
import { MatButton } from '@angular/material/button';
import { MatDialogClose } from '@angular/material/dialog';
import { CartSidenav } from '@shared/service/cart-sidenav';
import { BreakpointObserver } from '@angular/cdk/layout';
import { rxResource, takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { Cart } from '../../cart/cart/cart';
import { CatalogApi, CategoryWithSubs } from '@shared/service/catalog-api';

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
              @for (category of categories.value(); track category.id) {
                <mat-expansion-panel class="nav-list">
                  <mat-expansion-panel-header>
                    <mat-panel-title>{{ category.name }}</mat-panel-title>
                  </mat-expansion-panel-header>

                  @for (sub of category.subCategories; track sub) {
                    <a
                      mat-list-item
                      [routerLink]="['/products']"
                      [queryParams]="{ categories: sub.name }"
                    >
                      {{ sub.name }}
                    </a>
                  }
                </mat-expansion-panel>
              }
            </mat-nav-list>
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
  sidenavService = inject(SidenavService);
  cartSidenavService = inject(CartSidenav);
  catalogApi = inject(CatalogApi);

  @ViewChild('sidenav') sidenav!: MatSidenav;
  @ViewChild('cartSidenav') cartSidenav!: MatSidenav;

  categories = rxResource<CategoryWithSubs[], unknown>({
    stream: (params) => this.catalogApi.getCategoriesWithSubs(),
    defaultValue: [],
  });

  isMobile = signal(false);
  bpo = inject(BreakpointObserver);

  constructor() {
    this.bpo
      .observe('(max-width: 768px)')
      .pipe(takeUntilDestroyed())
      .subscribe((res) => this.isMobile.set(res.matches));
  }

  ngAfterViewInit() {
    this.sidenavService.setSidenav(this.sidenav);
    this.cartSidenavService.setSidenav(this.cartSidenav);
    // To remove the flash when rendering
    this.sidenav.close();
    this.cartSidenav.close();
  }
}
