import { Component } from '@angular/core';

@Component({
  selector: 'app-profile',
  imports: [],
  host: {
    class: 'min-h-screen flex items-center justify-center ',
  },
  template: ` <div class="w-full max-w-md">This is profile page</div> `,
})
export default class ProfilePage {}
