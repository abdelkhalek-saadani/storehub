import { Component } from '@angular/core';

@Component({
  selector: 'app-divider',
  imports: [],
  template: `
    <div class="flex items-center gap-2">
      <hr class="flex-1 text-white" />
      <span class="text-sm text-muted text-white">OR</span>
      <hr class="flex-1 text-white" />
    </div>
  `,
  styles: ``,
})
export class Divider {}
