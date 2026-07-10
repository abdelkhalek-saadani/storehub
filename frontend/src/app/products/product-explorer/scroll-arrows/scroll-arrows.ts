import { Component, output } from '@angular/core';
import { MatIcon } from '@angular/material/icon';
import { MatIconButton } from '@angular/material/button';

@Component({
  selector: 'app-scroll-arrows',
  imports: [MatIcon, MatIconButton],
  template: `
    <div class="flex items-center gap-4">
      <button matIconButton (click)="arrowClicked.emit('left')" class="scroll-arrow">
        <mat-icon>arrow_back_ios_new</mat-icon>
      </button>

      <button matIconButton (click)="arrowClicked.emit('right')" class="scroll-arrow">
        <mat-icon>arrow_forward_ios</mat-icon>
      </button>
    </div>
  `,
  styles: ``,
})
export class ScrollArrows {
  arrowClicked = output<'left' | 'right'>();
}
