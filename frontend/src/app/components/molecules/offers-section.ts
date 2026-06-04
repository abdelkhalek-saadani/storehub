import { Component, inject, OnInit, signal } from '@angular/core';
import { BreakpointObserver, Breakpoints } from '@angular/cdk/layout';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { OffersMasonry } from './offers-masonry';
import { OffersSlider } from './offers-slider';
import { Offer } from '../../shared/models/Offer';

@Component({
  selector: 'app-offers-section',
  standalone: true,
  imports: [OffersMasonry, OffersSlider],
  template: `
    @if (isMobile()) {
      <app-offers-slider [offers]="offers" />
    } @else {
      <app-offers-masonry [offers]="offers" />
    }
  `,
})
export class OffersSection {
  private breakpointObserver = inject(BreakpointObserver);
  isMobile = signal(false);

  constructor() {
    this.breakpointObserver
      .observe([Breakpoints.XSmall, Breakpoints.Small])
      .pipe(takeUntilDestroyed())
      .subscribe((result) => this.isMobile.set(result.matches));
  }

  offers: Offer[] = [
    {
      id: '1',
      label: "Mother's Day",
      imageUrl: '/offers/mothers-day.jpg',
      alt: "Mother's Day special offer",
      routerLink: '/offers/mothers-day',
    },
    {
      id: '2',
      label: 'Summer Sale',
      imageUrl: '/offers/summer.jpg',
      alt: 'Summer sale offers',
      routerLink: '/offers/summer',
    },
    {
      id: '3',
      label: "Father's Day",
      imageUrl: '/offers/fathers-day.jpg',
      alt: "Father's Day special offer",
      routerLink: '/offers/fathers-day',
    },
    {
      id: '4',
      label: 'Back to School',
      imageUrl: '/offers/back-to-school.jpg',
      alt: 'Back to school collection',
      routerLink: '/offers/back-to-school',
    },
    {
      id: '5',
      label: 'New Arrivals',
      imageUrl: '/offers/new-arrivals.jpg',
      alt: 'New arrivals',
      routerLink: '/offers/new-arrivals',
    },
    {
      id: '6',
      label: 'Special Offer',
      imageUrl: '/offers/special-offer.jpg',
      alt: 'Special offer',
      routerLink: '/offers/special-offer',
    },
  ];
}
