import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { MatSidenavModule } from '@angular/material/sidenav';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, MatSidenavModule],
  template: ` <router-outlet /> `,
  styles: [],
})
export class App {}
