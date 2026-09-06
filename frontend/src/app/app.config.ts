import {
  ApplicationConfig,
  inject,
  provideAppInitializer,
  provideBrowserGlobalErrorListeners,
  provideZonelessChangeDetection,
} from '@angular/core';
import { provideRouter, withComponentInputBinding, withViewTransitions } from '@angular/router';

import { routes } from './app.routes';
import { provideHotToastConfig } from '@ngxpert/hot-toast';
import { MAT_FORM_FIELD_DEFAULT_OPTIONS } from '@angular/material/form-field';
import { MAT_DIALOG_DEFAULT_OPTIONS } from '@angular/material/dialog';
import { DomSanitizer, provideClientHydration, withEventReplay } from '@angular/platform-browser';
import { provideHttpClient, withInterceptors } from '@angular/common/http';
import { MatIconRegistry } from '@angular/material/icon';
import { provideNativeDateAdapter } from '@angular/material/core';
import { includeBearerTokenInterceptor } from 'keycloak-angular';
import { guestIdInterceptor } from '@core/http/guest-id-interceptor';
import { CartMergeService } from '@shared/service/cart-merge.service';

export const appConfig: ApplicationConfig = {
  providers: [
    provideHttpClient(withInterceptors([includeBearerTokenInterceptor, guestIdInterceptor])),
    provideBrowserGlobalErrorListeners(),
    provideZonelessChangeDetection(),
    provideRouter(routes, withComponentInputBinding(), withViewTransitions()),
    provideHotToastConfig({ style: { marginTop: '70px' }, stacking: 'depth', duration: 1000 }),
    {
      provide: MAT_FORM_FIELD_DEFAULT_OPTIONS,
      useValue: {
        appearance: 'outline',
        subscriptSizing: 'dynamic',
        floatLabel: 'auto',
      },
    },
    { provide: MAT_DIALOG_DEFAULT_OPTIONS, useValue: { disableClose: false } },
    provideAppInitializer(() => {
      const registry = inject(MatIconRegistry);
      const sanitizer = inject(DomSanitizer);

      const icons = [
        ['meta', 'icons/meta.svg'],
        ['google', 'icons/google.svg'],
      ];

      icons.forEach(([name, path]) => {
        registry.addSvgIcon(name, sanitizer.bypassSecurityTrustResourceUrl(path));
      });

      inject(CartMergeService);
    }),
    provideNativeDateAdapter(),
  ],
};
