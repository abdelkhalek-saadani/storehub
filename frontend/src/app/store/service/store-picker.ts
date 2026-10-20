import { inject, Injectable } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { filter, Observable } from 'rxjs';
import { StorePickerModal } from '../modal/store-picker.modal';
import { Store } from '@shared/models/Store';

@Injectable({ providedIn: 'root' })
export class StorePickerService {
  private dialog = inject(MatDialog);

  pickStore(dismissible = false): Observable<Store> {
    const ref = this.dialog.open(StorePickerModal, {
      width: '480px',
      disableClose: !dismissible,
    });
    return ref.afterClosed().pipe(filter((store): store is Store => !!store));
  }
}
