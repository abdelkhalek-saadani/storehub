import { bootstrapApplication } from '@angular/platform-browser';
import { appConfig } from './app/app.config';
import { App } from './app/app';
import { createKeycloakProvider } from '@core/auth/keycloak-config';
import { AppConfig, ConfigService } from '@core/config.service';
import { createBearerInterceptorProvider } from '@core/http/bearer-interceptor-config';

fetch('/config.json')
  .then((res) => res.json())
  .then((config: AppConfig) => {
    const configService = new ConfigService();
    configService.setConfig(config);

    bootstrapApplication(App, {
      providers: [
        ...appConfig.providers,
        {
          provide: ConfigService,
          useValue: configService,
        },
        createBearerInterceptorProvider(config),
        createKeycloakProvider(config),
      ],
    }).catch((err) => console.error(err));
  });
