import {
  INCLUDE_BEARER_TOKEN_INTERCEPTOR_CONFIG,
  createInterceptorCondition,
  IncludeBearerTokenCondition,
} from 'keycloak-angular';
import { AppConfig } from '@core/config.service';

export function createBearerInterceptorProvider(config: AppConfig) {
  const apiBearerCondition = createInterceptorCondition<IncludeBearerTokenCondition>({
    urlPattern: new RegExp('^' + escapeRegex(config.orderApiUrl) + '(/.*)?$', 'i'),
    bearerPrefix: 'Bearer',
  });

  return {
    provide: INCLUDE_BEARER_TOKEN_INTERCEPTOR_CONFIG,
    useValue: [apiBearerCondition],
  };
}

export function escapeRegex(str: string): string {
  return str.replace(/[.*+?^${}()|[\]\\]/g, '\\$&');
}
