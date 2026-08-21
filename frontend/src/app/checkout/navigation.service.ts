import { Injectable } from '@angular/core';

@Injectable({ providedIn: 'root' })
export class NavigationService {
  redirectTo(url: string) {
    window.location.href = url;
  }
}
