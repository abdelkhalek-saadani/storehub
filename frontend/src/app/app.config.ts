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
import {
  AutoRefreshTokenService,
  createInterceptorCondition,
  INCLUDE_BEARER_TOKEN_INTERCEPTOR_CONFIG,
  IncludeBearerTokenCondition,
  includeBearerTokenInterceptor,
  provideKeycloak,
  UserActivityService,
  withAutoRefreshToken,
} from 'keycloak-angular';
import { keycloakProvider } from '@core/auth/keycloak-config';
import { bearerInterceptorProvider } from '@core/http/bearer-interceptor-config';
import { guestIdInterceptor } from '@core/http/guest-id-interceptor';
import { CartMergeService } from '@shared/service/cart-merge.service';

export const appConfig: ApplicationConfig = {
  providers: [
    bearerInterceptorProvider,
    keycloakProvider,
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
        floatLabel: 'auto', // either set it to always or auto
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
