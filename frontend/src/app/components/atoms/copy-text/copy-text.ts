import { Component, inject, input, signal } from '@angular/core';
import { MatIcon, MatIconModule } from '@angular/material/icon';
import { MatTooltip } from '@angular/material/tooltip';
import { Clipboard } from '@angular/cdk/clipboard';

@Component({
  selector: 'app-copy-text',

  imports: [MatIcon, MatTooltip, MatIconModule],
  template: `
    <div
      class="flex items-center gap-1 cursor-pointer text-primary text-[12px]"
      (click)="copy()"
      [matTooltip]="copied() ? 'Copied!' : 'Copy'"
    >
      <mat-icon [inline]="true">{{ copied() ? 'check' : 'content_copy' }}</mat-icon>
      <span (click)="copy()" class="">{{ text() }}</span>
    </div>
  `,
})
export class CopyText {
  text = input.required<string>();
  copied = signal(false);

  private clipboard = inject(Clipboard);

  copy() {
    this.clipboard.copy(this.text());
    this.copied.set(true);
    setTimeout(() => this.copied.set(false), 2000);
  }
}
