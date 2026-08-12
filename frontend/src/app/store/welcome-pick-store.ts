import { Component, inject } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { Router, ActivatedRoute } from '@angular/router';
import { StorePickerService } from './service/store-picker';
import { LogoText } from '@shared/components/logo-text/logo-text';

@Component({
  selector: 'app-welcome-pick-store',
  standalone: true,
  imports: [MatButtonModule, LogoText],
  template: `
    <div class="h-screen flex flex-col items-center justify-center gap-3 p-6 text-center">
      <app-logo-text [height]="40" [width]="163" class="mb-2" />
      <h1 class="text-3xl font-medium m-0">Welcome</h1>
      <p class="text-black/60 mb-4">Pick a store to explore</p>
      <button mat-flat-button color="primary" (click)="choose()">Choose a store</button>
    </div>
  `,
  styles: ``,
})
export default class WelcomePickStore {
  private picker = inject(StorePickerService);
  private router = inject(Router);
  private route = inject(ActivatedRoute);

  choose(): void {
    const returnUrl = this.route.snapshot.queryParamMap.get('returnUrl') ?? '/products-explorer';
    this.picker.pickStore(false).subscribe((store) => {
      this.router.navigateByUrl(`/store/${store.storeSlug}${returnUrl}`);
    });
  }
}
