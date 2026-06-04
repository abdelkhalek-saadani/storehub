import { Component, inject, signal } from '@angular/core';
import { BreakpointObserver, Breakpoints } from '@angular/cdk/layout';
import { AsyncPipe } from '@angular/common';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatButtonModule } from '@angular/material/button';
import { MatSidenavModule } from '@angular/material/sidenav';
import { MatListModule } from '@angular/material/list';
import { MatIconModule } from '@angular/material/icon';
import { Observable } from 'rxjs';
import { map, shareReplay } from 'rxjs/operators';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';

@Component({
  selector: 'app-navigation-test-schematic',
  template: `
    <mat-sidenav-container class="sidenav-container">
      <mat-sidenav
        #drawer
        class="sidenav"
        fixedInViewport
        [attr.role]="isHandset() ? 'dialog' : 'navigation'"
        [mode]="isHandset() ? 'over' : 'side'"
        [opened]="!isHandset()"
      >
        <mat-toolbar>Menu</mat-toolbar>
        <mat-nav-list>
          <a mat-list-item href="#">Link 1</a>
          <a mat-list-item href="#">Link 2</a>
          <a mat-list-item href="#">Link 3</a>
        </mat-nav-list>
      </mat-sidenav>
      <mat-sidenav-content>
        <mat-toolbar color="primary">
          @if (isHandset()) {
            <button
              type="button"
              aria-label="Toggle sidenav"
              matIconButton
              (click)="drawer.toggle()"
            >
              <mat-icon aria-label="Side nav toggle icon">menu</mat-icon>
            </button>
          }
          <span>frontend</span>
        </mat-toolbar>
        <!-- Add Content Here -->
      </mat-sidenav-content>
    </mat-sidenav-container>
  `,
  styles: `
    .sidenav-container {
      height: 100%;
    }

    .sidenav {
      width: 200px;
    }

    .sidenav .mat-toolbar {
      background: inherit;
    }

    .mat-toolbar.mat-primary {
      position: sticky;
      top: 0;
      z-index: 1;
    }
  `,
  imports: [MatToolbarModule, MatButtonModule, MatSidenavModule, MatListModule, MatIconModule],
})
export class NavigationTestSchematicComponent {
  isHandset = signal(false);

  constructor(bpo: BreakpointObserver) {
    bpo
      .observe(Breakpoints.Handset)
      .pipe(takeUntilDestroyed())
      .subscribe((result) => this.isHandset.set(result.matches));
  }
}
