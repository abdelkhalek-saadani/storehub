import { Component, inject, signal } from '@angular/core';
import { MatDialogContent, MatDialogRef, MatDialogTitle } from '@angular/material/dialog';
import { StoreApi } from '../service/store-api';
import { Store } from '@shared/models/Store';
import { MatProgressSpinner } from '@angular/material/progress-spinner';
import { MatListItem, MatNavList } from '@angular/material/list';

@Component({
  selector: 'app-store-picker-modal',
  standalone: true,
  imports: [MatDialogContent, MatDialogTitle, MatProgressSpinner, MatNavList, MatListItem],
  template: ` <h2 mat-dialog-title>Choose a store</h2>
    <mat-dialog-content>
      @if (loading()) {
        <mat-spinner diameter="32" />
      } @else {
        <mat-nav-list>
          @for (store of stores(); track store.storeId) {
            <a mat-list-item (click)="select(store)">{{ store.storeName }}</a>
          }
        </mat-nav-list>
      }
    </mat-dialog-content>`,
})
export class StorePickerModal {
  private dialogRef = inject(MatDialogRef<StorePickerModal>);
  private storeApi = inject(StoreApi);

  readonly stores = signal<Store[]>([]);
  readonly loading = signal(true);

  constructor() {
    this.storeApi.getAllStores().subscribe({
      next: (stores) => {
        this.stores.set(stores);
        this.loading.set(false);
      },
      error: () => this.loading.set(false),
    });
  }

  select(store: Store): void {
    this.dialogRef.close(store);
  }
}
