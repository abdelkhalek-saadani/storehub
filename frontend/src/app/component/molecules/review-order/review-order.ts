import { Component } from '@angular/core';
import { MatExpansionPanel, MatExpansionPanelHeader } from '@angular/material/expansion';

@Component({
  selector: 'app-review-order',
  imports: [MatExpansionPanel, MatExpansionPanelHeader],
  template: `
    <div class="flex flex-col p-6 gap-6 border border-[#F0EEF0] rounded-xl">
      <div class="font-semibold text-lg">Review Order</div>
      <div class="flex flex-row">
        <mat-expansion-panel>
          <mat-expansion-panel-header>The header</mat-expansion-panel-header>
          The Content
        </mat-expansion-panel>
      </div>
    </div>
  `,
  styles: ``,
})
export class ReviewOrder {}
