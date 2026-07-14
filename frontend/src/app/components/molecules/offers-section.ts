import { Component, inject, OnInit, signal } from '@angular/core';
import { BreakpointObserver, Breakpoints } from '@angular/cdk/layout';
import { rxResource, takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { OffersMasonry } from './offers-masonry';
import { OffersSlider } from './offers-slider';
import { Offer } from '../../shared/models/Offer';
import { ProductService } from '../../products/service/catalog-api';

@Component({
  selector: 'app-offers-section',
  standalone: true,
  imports: [OffersMasonry, OffersSlider],
  template: `
    @if (isMobile()) {
      <app-offers-slider [offers]="offers.value()" />
    } @else {
      <app-offers-masonry [offers]="offers.value()" />
    }
  `,
})
export class OffersSection {
  private breakpointObserver = inject(BreakpointObserver);
  private catalogApi = inject(ProductService);
  isMobile = signal(false);

  constructor() {
    this.breakpointObserver
      .observe([Breakpoints.XSmall, Breakpoints.Small])
      .pipe(takeUntilDestroyed())
      .subscribe((result) => this.isMobile.set(result.matches));
  }

  offers = rxResource<Offer[], any>({
    stream: (params) => this.catalogApi.getSaleEvent(),
    defaultValue: [],
  });
}
