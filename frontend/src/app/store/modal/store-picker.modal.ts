import { Component, inject, signal } from '@angular/core';
import { MatDialogContent, MatDialogRef, MatDialogTitle } from '@angular/material/dialog';
import { StoreApi } from '../service/store-api';
import { Store } from '@shared/models/Store';
import { MatProgressSpinner } from '@angular/material/progress-spinner';
import { getStoreColor, getStoreInitials } from '../utils/store-avatar';
import { MatButton } from '@angular/material/button';
import { MatIcon } from '@angular/material/icon';

@Component({
  selector: 'app-store-picker-modal',
  standalone: true,
  imports: [MatDialogContent, MatDialogTitle, MatProgressSpinner, MatButton, MatIcon],
  template: `
    <h2 mat-dialog-title>Choose a store</h2>
    <mat-dialog-content>
      @if (loading()) {
        <div class="flex flex-col items-center gap-3 py-8 text-black/60">
          <mat-spinner diameter="32" />
        </div>
      } @else if (error()) {
        <div class="flex flex-col items-center gap-3 py-8 text-black/60">
          <p>Couldn't load stores.</p>
          <button mat-stroked-button (click)="retry()">Retry</button>
        </div>
      } @else if (stores().length === 0) {
        <div class="flex flex-col items-center gap-3 py-8 text-black/60">
          <p>No stores available yet.</p>
        </div>
      } @else {
        <ul class="list-none m-0 p-0 min-w-[320px]">
          @for (store of stores(); track store.storeId) {
            <li
              class="flex items-center gap-3 py-[10px] px-2 rounded-lg cursor-pointer  [transition:background_0.15s_ease] hover:bg-black/4"
              (click)="select(store)"
            >
              <span
                class="w-9 h-9 rounded-[50%] flex items-center justify-center text-white text-sm font-semibold shrink-0"
                [style.background]="getColor(store.storeId)"
              >
                {{ getInitials(store.storeName) }}
              </span>
              <span class="flex-1 text-[0.95rem]">{{ store.storeName }}</span>
              <mat-icon class="text-black/30">chevron_right</mat-icon>
            </li>
          }
        </ul>
      }
    </mat-dialog-content>
  `,
  styles: ``,
})
export class StorePickerModal {
  private dialogRef = inject(MatDialogRef<StorePickerModal>);
  private storeApi = inject(StoreApi);

  readonly stores = signal<Store[]>([]);
  readonly loading = signal(true);
  readonly error = signal(false);

  constructor() {
    this.load();
  }

  private load(): void {
    this.loading.set(true);
    this.error.set(false);
    this.storeApi.getAllStores().subscribe({
      next: (stores) => {
        this.stores.set(stores);
        this.loading.set(false);
      },
      error: () => {
        this.error.set(true);
        this.loading.set(false);
      },
    });
  }

  retry(): void {
    this.load();
  }

  select(store: Store): void {
    this.dialogRef.close(store);
  }

  getInitials(name: string): string {
    return getStoreInitials(name);
  }

  getColor(id: string): string {
    return getStoreColor(id);
  }
}
