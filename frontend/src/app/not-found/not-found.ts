import { Component } from '@angular/core';
@Component({
  selector: 'app-not-found',
  host: {
    class: 'min-h-screen flex flex-col gap-6 px-4 py-4 bg-[#FEFCFE]',
  },
  template: `
    <div
      class="bg-white flex justify-between p-6 items-center rounded-[32px] border border-[#F8F7F8] "
    >
      Not Found!
    </div>
  `,
})
export default class NotFound {}
