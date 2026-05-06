import {Component} from '@angular/core';
import {RouterOutlet} from '@angular/router';
import {Header} from './layout/header/header';
import {MatSidenavModule} from '@angular/material/sidenav';


@Component({
  selector: 'app-root',
  imports: [RouterOutlet, MatSidenavModule],
  template: `
      <router-outlet />
  `,
  styles: [],
})
export class App {

}
