import { Component, signal } from '@angular/core';
import { OffersSection } from './offers-section/offers-section';
import { CategoryBar } from '@components/molecules/category-bar/category-bar';
import { BestSellerSection } from './best-seller-section/best-seller-section';
import { ProductsSection } from './products-section/products-section';

@Component({
  selector: 'app-product-explorer',
  imports: [OffersSection, CategoryBar, BestSellerSection, ProductsSection],
  host: {
    class: 'min-h-screen flex flex-col px-4 bg-[#F8F8F8]',
  },
  template: `
    <app-offers-section />
    <app-category-bar />
    <app-best-seller-section />
    <app-products-section />
  `,
})
export default class ProductExplorerPage {}
