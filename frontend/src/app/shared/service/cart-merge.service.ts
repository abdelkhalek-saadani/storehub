import { inject, Injectable, effect } from '@angular/core';
import { KEYCLOAK_EVENT_SIGNAL, typeEventArgs, KeycloakEventType } from 'keycloak-angular';
import { GUEST_ID_KEY } from '../../config/http/guest-id-interceptor';
import { OrderApi } from '@shared/service/order-api';

@Injectable({ providedIn: 'root' })
export class CartMergeService {
  private keycloakSignal = inject(KEYCLOAK_EVENT_SIGNAL);
  private orderApi = inject(OrderApi);

  constructor() {
    effect(() => {
      const event = this.keycloakSignal();
      if (
        event.type === KeycloakEventType.AuthSuccess ||
        event.type === KeycloakEventType.AuthRefreshSuccess
      ) {
        this.mergeGuestCartIfNeeded();
      }
    });
  }

  private mergeGuestCartIfNeeded(): void {
    const guestId = localStorage.getItem(GUEST_ID_KEY);
    if (!guestId) return;

    this.orderApi.mergeCart().subscribe({
      next: () => localStorage.removeItem(GUEST_ID_KEY),
      error: () => {},
    });
  }
}
