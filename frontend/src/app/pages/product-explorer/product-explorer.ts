import { Component, signal } from '@angular/core';
import { BreakpointObserver, Breakpoints } from '@angular/cdk/layout';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { OffersSection } from '../../components/molecules/offers-section';
import { CategoryBar } from '../../components/molecules/category-bar/category-bar';

@Component({
  selector: 'app-product-explorer',
  imports: [OffersSection, CategoryBar],
  host: {
    class: 'min-h-screen flex flex-col px-4',
  },
  template: `
    <app-offers-section />
    <app-category-bar />
    <div>Best Seller Section</div>
    <div>Product Section</div>
  `,
})
export default class ProductExplorerPage {
  isMobile = signal(false);

  constructor(bpo: BreakpointObserver) {
    bpo
      .observe([Breakpoints.XSmall, Breakpoints.Small])
      .pipe(takeUntilDestroyed())
      .subscribe((res) => this.isMobile.set(res.matches));
  }
}
