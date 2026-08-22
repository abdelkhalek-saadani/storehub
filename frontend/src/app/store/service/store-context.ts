import { Injectable, signal } from '@angular/core';
import { Store } from '@shared/models/Store';

const STORAGE_KEY = 'current_store';

export interface CurrentStore {
  id?: string;
  slug?: string;
  name?: string;
}

@Injectable({ providedIn: 'root' })
export class StoreContext {
  readonly storeId = signal<string | null>(null);
  readonly storeSlug = signal<string | null>(null);
  readonly storeName = signal<string | null>(null);

  setStore({ storeId, storeSlug, storeName }: Store): void {
    this.storeId.set(storeId);
    this.storeSlug.set(storeSlug);
    this.storeName.set(storeName);

    this.saveCurrentStore({
      id: storeId,
      slug: storeSlug,
      name: storeName,
    });
  }

  saveCurrentStore(data: CurrentStore) {
    localStorage.setItem(STORAGE_KEY, JSON.stringify(data));
  }

  getCurrentStore(): CurrentStore | null {
    const raw = localStorage.getItem(STORAGE_KEY);
    if (!raw) return null;

    try {
      return JSON.parse(raw);
    } catch (err) {
      console.error('Corrupted current store data, clearing', err);
      localStorage.removeItem(STORAGE_KEY);
      return null;
    }
  }

  clearCurrentStore() {
    localStorage.removeItem(STORAGE_KEY);
  }
}
