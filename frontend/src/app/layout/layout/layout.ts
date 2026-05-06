import {AfterViewInit, Component, inject, input, ViewChild} from '@angular/core';
import {MatListItem, MatNavList} from '@angular/material/list';
import {MatSidenav, MatSidenavContainer, MatSidenavContent} from '@angular/material/sidenav';
import {TitleCasePipe} from '@angular/common';
import {ProductStore} from '../../product-store';
import {RouterLink, RouterLinkActive, RouterOutlet} from '@angular/router';
import {SidenavService} from '../../services/Sidenav';
import {Header} from '../header/header';

@Component({
  selector: 'app-sidenav-container',
  imports: [
    MatListItem,
    MatNavList,
    MatSidenav,
    MatSidenavContainer,
    MatSidenavContent,
    TitleCasePipe,
    RouterOutlet,
    RouterLink,
    Header
  ],
  template: `
    <app-header class="relative z-10"/>
    <div class="h-[calc(100%-64px)] overflow-auto">
      <mat-sidenav-container class="h-full">

        <mat-sidenav #sidenav mode="side" >

          <div class="p-6">
            <h2 class="text-lg text-gray-900">Categories</h2>
            <mat-nav-list aria-label="Select a folder">

              @for (cat of store.categories(); track $index) {

                <mat-list-item [activated]="store.category()==cat.name" class="my-2" [routerLink]="cat.link">
                <span [class]="store.category()==cat.name? 'text-white': null"
                      matListItemTitle>{{ cat.name | titlecase }}</span>
                </mat-list-item>

              }

            </mat-nav-list>
          </div>
        </mat-sidenav>
        <mat-sidenav-content>
          <router-outlet/>
        </mat-sidenav-content>
      </mat-sidenav-container>
    </div>
  `,
  styles: ``,
})
export default class Layout implements AfterViewInit {
  store = inject(ProductStore);
  sidenavService = inject(SidenavService);
  @ViewChild('sidenav') sidenav!: MatSidenav;


  constructor() {

  }

  ngAfterViewInit() {
    this.sidenavService.setSidenav(this.sidenav);
    // To remove the flash when rendering
    this.sidenav.close();
  }
}
