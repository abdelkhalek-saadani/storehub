import { Injectable } from '@angular/core';
import {MatSidenav} from '@angular/material/sidenav';

@Injectable({
  providedIn: 'root',
})
export class SidenavService {
  private sidenav!: MatSidenav;

  setSidenav(sidenav: MatSidenav) {
    this.sidenav = sidenav;
  }

  toggle() {
    console.log('toggle called, sidenav is:', this.sidenav);
    this.sidenav?.toggle();
  }

  open() {
    this.sidenav.open();
  }

  close() {
    this.sidenav.close();
  }

}
