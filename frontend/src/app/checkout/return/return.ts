import { Component, inject, OnInit } from '@angular/core';
import { HttpParams } from '@angular/common/http';
import { ActivatedRoute, Router } from '@angular/router';
import { StoreContext } from '../../products/service/store-context';

@Component({
  selector: 'app-return',
  imports: [],
  template: ` <p>Payment successful, redirecting...</p> `,
  styles: ``,
})
export default class Return implements OnInit {
  private route = inject(ActivatedRoute);
  token: string | null = null;
  router = inject(Router);
  storeContext = inject(StoreContext);

  ngOnInit() {
    this.route.queryParamMap.subscribe((paramMap) => {
      this.token = paramMap.get('token');
    });
    const currentStore = this.storeContext.getCurrentStore();
    if (currentStore) {
      this.router.navigate(['store', currentStore.slug, 'track-order'], {
        queryParams: { token: this.token },
      });
      return;
    }
    this.router.navigate(['checkout', 'return', 'success']);
  }
}
