import { Component, inject, OnInit } from '@angular/core';
import { HttpParams } from '@angular/common/http';
import { ActivatedRoute, Router } from '@angular/router';
import { StoreContext } from '../../store/service/store-context';
import Keycloak from 'keycloak-js';

@Component({
  selector: 'app-return',
  imports: [],
  template: ` <p>Payment successful, redirecting...</p> `,
  styles: ``,
})
export default class Return implements OnInit {
  private route = inject(ActivatedRoute);
  token: string | null = null;
  orderId: string | null = null;
  router = inject(Router);
  storeContext = inject(StoreContext);
  keycloak = inject(Keycloak);

  ngOnInit() {
    const currentStore = this.storeContext.getCurrentStore();
    if (!currentStore) {
      this.router.navigate(['checkout', 'return', 'success']);
      return;
    }
    this.route.queryParamMap.subscribe((paramMap) => {
      this.token = paramMap.get('token');
      this.orderId = paramMap.get('orderId');
    });
    if (this.keycloak.authenticated) {
      this.router.navigate(['store', currentStore.slug, 'track-order'], {
        queryParams: { token: this.token },
      });
      return;
    } else {
      this.router.navigate(['store', currentStore.slug, 'guest-track-order'], {
        queryParams: { orderId: this.orderId },
      });
      return;
    }
  }
}
