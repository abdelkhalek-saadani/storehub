import {
  createInterceptorCondition,
  INCLUDE_BEARER_TOKEN_INTERCEPTOR_CONFIG,
  IncludeBearerTokenCondition,
} from 'keycloak-angular';
import { environment } from '@environments/environment';

const apiBearerCondition = createInterceptorCondition<IncludeBearerTokenCondition>({
  urlPattern: new RegExp('^' + escapeRegex(environment.orderApiUrl) + '(/.*)?$', 'i'),
  bearerPrefix: 'Bearer',
});

export const bearerInterceptorProvider = {
  provide: INCLUDE_BEARER_TOKEN_INTERCEPTOR_CONFIG,
  useValue: [apiBearerCondition],
};

export function escapeRegex(str: string): string {
  return str.replace(/[.*+?^${}()|[\]\\]/g, '\\$&');
}
