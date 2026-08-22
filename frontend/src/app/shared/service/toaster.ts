import { inject, Injectable } from '@angular/core';
import { HotToastService } from '@ngxpert/hot-toast';

@Injectable({
  providedIn: 'root',
})
export class Toaster {
  toaster = inject(HotToastService);

  success(message: string, duration: number = 3000) {
    this.toaster.success(message, { duration: duration });
  }

  error(message: string) {
    this.toaster.error(message);
  }
}
