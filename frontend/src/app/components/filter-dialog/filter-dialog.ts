import { Component } from '@angular/core';
import {
  MatDialogActions,
  MatDialogClose,
  MatDialogContent,
  MatDialogTitle,
} from '@angular/material/dialog';
import { MatButton, MatIconButton } from '@angular/material/button';
import { MatIcon } from '@angular/material/icon';
import { MatCheckbox } from '@angular/material/checkbox';
import { MatDivider } from '@angular/material/divider';

@Component({
  selector: 'app-filter-dialog',
  imports: [
    MatDialogContent,
    MatDialogTitle,
    MatDialogActions,
    MatButton,
    MatDialogClose,
    MatIcon,
    MatIconButton,
    MatCheckbox,
    MatDivider,
  ],
  template: `
    <div class="flex items-center justify-between">
      <span mat-dialog-title>Filters</span>
      <button matIconButton [mat-dialog-close]="false" class="btn-close">
        <mat-icon>close</mat-icon>
      </button>
    </div>
    <mat-dialog-content>
      <div class="flex flex-col gap-3">
        <div class="flex flex-col">
          <span class="p-3 pb-2 font-normal text-[#71717A] text-[16px]"
            >Filter products by category</span
          >
          <mat-checkbox>Cat1</mat-checkbox>
          <mat-checkbox>Cat2</mat-checkbox>
          <mat-divider></mat-divider>
        </div>
        <div class="flex flex-col">
          <span class="p-3 pb-2 font-normal text-[#71717A] text-[16px]"
            >Filter products by category</span
          >
          <mat-checkbox>Cat1</mat-checkbox>
          <mat-checkbox>Cat2</mat-checkbox>
        </div>
      </div>
    </mat-dialog-content>
    <mat-dialog-actions align="center">
      <button matButton [mat-dialog-close]="true" cdkFocusInitial>Apply</button>
      <button matButton mat-dialog-close>Reset</button>
    </mat-dialog-actions>
  `,
  styles: ``,
})
export class FilterDialog {}
