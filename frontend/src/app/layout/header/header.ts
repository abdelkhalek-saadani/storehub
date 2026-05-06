import {Component, inject} from '@angular/core';
import {MatToolbar} from '@angular/material/toolbar';
import {MatIcon} from '@angular/material/icon';
import { MatIconButton} from '@angular/material/button';
import {HeaderActions} from '../header-actions/header-actions';
import {ProductStore} from '../../product-store';
import {SidenavService} from '../../services/Sidenav';

@Component({
  selector: 'app-header',
  imports: [
    MatToolbar,
    MatIcon,
    MatIconButton,
    HeaderActions
  ],
  template: `
    <mat-toolbar class="w-full elevated">
      <div class="max-w-[1200px] w-full flex items-center justify-between mx-auto ">
        <div class="flex items-center">
          <button matIconButton (click)="sidenavService.toggle()">
            <mat-icon>menu</mat-icon>
          </button>
          <h1>EasyMart</h1>
        </div>
        <app-header-actions />
      </div>
    </mat-toolbar>

  `,
  styles: ``,
})
export class Header {
  store = inject(ProductStore);
  sidenavService = inject(SidenavService);

}
