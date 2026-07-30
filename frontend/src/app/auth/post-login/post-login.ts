import { Component, OnInit, inject } from '@angular/core';
import { Router } from '@angular/router';
import { StoreApi } from '@shared/service/store-api';
import { PendingStoreStorage } from '../pending-store-storage';
import { lastValueFrom } from 'rxjs';

@Component({
  selector: 'app-post-login',
  standalone: true,
  template: `<p>Setting things up…</p>`,
})
export class PostLogin implements OnInit {
  private storeApi = inject(StoreApi);
  private pendingStoreStorage = inject(PendingStoreStorage);
  private router = inject(Router);

  async ngOnInit() {
    const pending = this.pendingStoreStorage.getPendingStore();

    if (pending) {
      try {
        await lastValueFrom(this.storeApi.createStore(pending));
      } catch (err) {
        // Store creation failed post-login, don't block the user, just let
        // them retry from a dedicated "create your store" screen later.
        // TODO: add redirection to create your store page
        console.error('Pending store creation failed', err);
      } finally {
        this.pendingStoreStorage.clearPendingStore();
      }
    }

    this.router.navigate(['/dev']);
  }
}
