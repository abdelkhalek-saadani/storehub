import { Component } from '@angular/core';

@Component({
  selector: 'app-divider',
  imports: [],
  template: `
    <div class="flex items-center gap-2">
      <hr class="flex-1 border-border">
      <span class="text-sm text-muted">OR</span>
      <hr class="flex-1 border-border">
    </div>
  `,
  styles: ``,
})
export class Divider {

}
