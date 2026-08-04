import { Component, input, Input, OnInit } from '@angular/core';
import { MatIcon } from '@angular/material/icon';

@Component({
  selector: 'app-location-summary',
  imports: [MatIcon],
  template: `
    <div class="flex flex-col gap-2 p-4 border border-[#F8F7F8] rounded-2xl bg-white">
      <span class="text-base font-semibold">Delivery Address</span>
      <div class="text-primary text-[12px] flex items-center gap-1">
        <mat-icon>location_on</mat-icon>
        <span class="font-medium">{{ deliveryAddress() }}</span>
      </div>
    </div>
  `,
  styles: ``,
})
export class LocationSummary implements OnInit {
  deliveryAddress = input<string | null>();

  ngOnInit() {}
}
