import {
  AutoRefreshTokenService,
  provideKeycloak,
  UserActivityService,
  withAutoRefreshToken,
} from 'keycloak-angular';
import { environment } from '@environments/environment';
import { AppConfig } from '@core/config.service';

export function createKeycloakProvider(config: AppConfig) {
  return provideKeycloak({
    config: {
      url: config.kcUrl,
      realm: environment.keycloak.realm,
      clientId: environment.keycloak.clientId,
    },
    initOptions: {
      onLoad: 'check-sso',
      silentCheckSsoRedirectUri: window.location.origin + '/silent-check-sso.html',
    },
    features: [
      withAutoRefreshToken({
        onInactivityTimeout: 'logout',
        sessionTimeout: 600000,
      }),
    ],
    providers: [AutoRefreshTokenService, UserActivityService],
  });
}
