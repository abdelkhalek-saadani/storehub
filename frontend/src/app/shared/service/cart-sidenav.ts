import { DestroyRef, inject, Injectable } from '@angular/core';
import { MatSidenav } from '@angular/material/sidenav';
import { NavigationEnd, Router, Event } from '@angular/router';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { filter } from 'rxjs/operators';

@Injectable({
  providedIn: 'root',
})
export class CartSidenav {
  private sidenav!: MatSidenav;
  private router = inject(Router);
  private destroyRef = inject(DestroyRef);

  constructor() {
    this.router.events
      .pipe(
        filter((event: Event): event is NavigationEnd => event instanceof NavigationEnd),
        takeUntilDestroyed(this.destroyRef),
      )
      .subscribe((event: NavigationEnd) => {
        if (this.sidenav) this.close();
      });
  }

  setSidenav(sidenav: MatSidenav) {
    this.sidenav = sidenav;
  }

  toggle() {
    this.sidenav?.toggle();
  }

  open() {
    this.sidenav.open();
  }

  close() {
    this.sidenav.close();
  }
}
