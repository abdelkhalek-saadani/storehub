import {
  ApplicationConfig, inject,
  provideAppInitializer,
  provideBrowserGlobalErrorListeners,
  provideZonelessChangeDetection
} from '@angular/core';
import {provideRouter, withComponentInputBinding, withViewTransitions} from '@angular/router';

import {routes} from './app.routes';
import {provideHotToastConfig} from '@ngxpert/hot-toast';
import {MAT_FORM_FIELD_DEFAULT_OPTIONS} from '@angular/material/form-field';
import {MAT_DIALOG_DEFAULT_OPTIONS} from '@angular/material/dialog';
import {DomSanitizer, provideClientHydration, withEventReplay} from '@angular/platform-browser';
import {provideHttpClient} from '@angular/common/http';
import {MatIconRegistry} from '@angular/material/icon';
import {provideNativeDateAdapter} from '@angular/material/core';

export const appConfig: ApplicationConfig = {
  providers: [
    provideBrowserGlobalErrorListeners(),
    provideZonelessChangeDetection(),
    provideRouter(routes, withComponentInputBinding(), withViewTransitions()),
    provideHotToastConfig({style: {marginTop: '70px'}, stacking: 'depth', duration: 1000}),
    {
      provide: MAT_FORM_FIELD_DEFAULT_OPTIONS,
      useValue: {
        appearance: 'outline',
        subscriptSizing: 'dynamic',
        floatLabel: 'auto'  // either set it to always or auto
      }
    },
    {provide: MAT_DIALOG_DEFAULT_OPTIONS, useValue: {disableClose: true}},
    provideHttpClient(),
    provideAppInitializer(() => {
      const registry = inject(MatIconRegistry);
      const sanitizer = inject(DomSanitizer);

      const icons = [
        ['meta', 'icons/meta.svg'],
        ['google','icons/google.svg']
      ];

      icons.forEach(([name, path]) => {
        registry.addSvgIcon(
          name,
          sanitizer.bypassSecurityTrustResourceUrl(path)
        );
      });
    }),
    provideNativeDateAdapter()
  ]
};
