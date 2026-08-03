import { Component } from '@angular/core';
import { MatButton } from '@angular/material/button';
import { MatIcon } from '@angular/material/icon';

@Component({
  selector: 'app-invoice-download',
  imports: [MatButton, MatIcon],
  template: `
    <div class="flex items-center justify-between px-3 py-6 border-[#F8F7F8] rounded-2xl bg-white">
      <div class="flex flex-col gap-3">
        <span class="font-semibold text-[16px]">Download Invoice</span>
        <div class="flex gap-2 font-medium text-base text-primary">
          <mat-icon [inline]="true">picture_as_pdf</mat-icon>
          <span>invoice.pdf</span>
        </div>
      </div>
      <button matButton="filled" class="btn-icon">
        Download
        <mat-icon iconPositionEnd>download</mat-icon>
      </button>
    </div>
  `,
  styles: ``,
})
export class InvoiceDownload {}
