import { Injectable } from '@angular/core';

export const STORAGE_KEY = 'pending_store_creation';

export interface PendingStore {
  name: string;
  description: string;
  address: string;
}

@Injectable({ providedIn: 'root' })
export class PendingStoreStorage {
  savePendingStore(data: PendingStore) {
    sessionStorage.setItem(STORAGE_KEY, JSON.stringify(data));
  }

  getPendingStore(): PendingStore | null {
    const raw = sessionStorage.getItem(STORAGE_KEY);
    if (!raw) return null;

    try {
      return JSON.parse(raw);
    } catch (err) {
      console.error('Corrupted pending store data, clearing', err);
      sessionStorage.removeItem(STORAGE_KEY);
      return null;
    }
  }

  clearPendingStore() {
    sessionStorage.removeItem(STORAGE_KEY);
  }
}
