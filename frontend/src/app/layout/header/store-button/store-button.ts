import { Component, inject } from '@angular/core';
import { MatButton } from '@angular/material/button';
import { MatIcon } from '@angular/material/icon';
import { MatDialog } from '@angular/material/dialog';
import { StorePickerService } from '../../../store/service/store-picker';
import { StoreContext } from '../../../store/service/store-context';
import { Router } from '@angular/router';

@Component({
  selector: 'app-store-button',
  imports: [MatButton, MatIcon],
  template: `
    <button matButton="outlined" class="btn-sm" (click)="changeStore()">
      {{ storeContext.storeName() }}
      <mat-icon>storefront</mat-icon>
    </button>
  `,
  styles: ``,
})
export class StoreButton {
  picker = inject(StorePickerService);
  storeContext = inject(StoreContext);
  router = inject(Router);

  changeStore(): void {
    this.picker.pickStore(true).subscribe((store) => {
      this.storeContext.setStore(store);
      // swap slug in current url, keep rest of path
      const segments = this.router.url.split('/');
      // this works as long as the current path format is /store/:slug/...
      segments[2] = store.storeSlug;
      console.log('the new route {}', segments.join('/'));
      this.router.navigateByUrl(segments.join('/'));
    });
  }
}
