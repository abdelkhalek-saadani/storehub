import { Injectable, signal } from '@angular/core';

const STORAGE_KEY = 'current_store';

export interface CurrentStore {
  id?: string;
  slug?: string;
}

@Injectable({ providedIn: 'root' })
export class StoreContext {
  readonly storeId = signal<string | null>(null);
  readonly storeSlug = signal<string | null>(null);

  setStoreId(storeId: string): void {
    this.storeId.set(storeId);
    const currentStore = this.getCurrentStore();
    if (currentStore !== null) {
      this.saveCurrentStore({
        ...currentStore,
        id: storeId,
      });
    } else {
      this.saveCurrentStore({
        id: storeId,
      });
    }
  }

  setStoreSlug(slug: string): void {
    this.storeSlug.set(slug);
    const currentStore = this.getCurrentStore();
    if (currentStore !== null) {
      this.saveCurrentStore({
        ...currentStore,
        slug,
      });
    } else {
      this.saveCurrentStore({
        slug,
      });
    }
  }

  saveCurrentStore(data: CurrentStore) {
    sessionStorage.setItem(STORAGE_KEY, JSON.stringify(data));
  }

  getCurrentStore(): CurrentStore | null {
    const raw = sessionStorage.getItem(STORAGE_KEY);
    if (!raw) return null;

    try {
      return JSON.parse(raw);
    } catch (err) {
      console.error('Corrupted current store data, clearing', err);
      sessionStorage.removeItem(STORAGE_KEY);
      return null;
    }
  }

  clearPendingStore() {
    sessionStorage.removeItem(STORAGE_KEY);
  }
}
