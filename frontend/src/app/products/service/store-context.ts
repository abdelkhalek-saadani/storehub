import { Injectable, signal } from '@angular/core';

@Injectable({ providedIn: 'root' })
export class StoreContext {
  readonly storeId = signal<string | null>(null);
  readonly storeSlug = signal<string | null>(null);

  setStoreId(storeId: string): void {
    this.storeId.set(storeId);
  }

  setStoreSlug(slug: string): void {
    this.storeSlug.set(slug);
  }
}
