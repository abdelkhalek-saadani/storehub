import { Component, input, AfterViewInit, ElementRef, viewChild } from '@angular/core';
import { RouterLink } from '@angular/router';
import { Offer } from '@shared/models/Offer';
import Swiper from 'swiper';
import { Pagination, A11y } from 'swiper/modules';

@Component({
  selector: 'app-offers-slider',
  standalone: true,
  imports: [RouterLink],
  template: `
    <section aria-label="Special offers">
      <div class="swiper h-60 w-full rounded-lg overflow-hidden" #swiperEl>
        <div class="swiper-wrapper ">
          @for (offer of offers(); track offer.id) {
            <div class="swiper-slide">
              <a
                class="block w-full h-full no-underline"
                routerLink="../products"
                [queryParams]="{ saleEvent: offer.slug }"
                [attr.aria-label]="offer.description"
              >
                <img
                  class="object-cover h-full w-full"
                  [src]="offer.imageUrl"
                  [alt]="offer.description"
                  loading="lazy"
                />
                <span class="absolute bottom-4 left-4 text-white text-base font-medium">
                  {{ offer.name }}
                </span>
              </a>
            </div>
          }
        </div>
        <div #paginationEl class="swiper-pagination"></div>
      </div>
    </section>
  `,
  styles: [``],
})
export class OffersSlider implements AfterViewInit {
  offers = input.required<Offer[]>();
  private swiperEl = viewChild.required<ElementRef>('swiperEl');
  private paginationEl = viewChild.required<ElementRef>('paginationEl');

  ngAfterViewInit() {
    new Swiper(this.swiperEl().nativeElement, {
      modules: [Pagination, A11y],
      slidesPerView: 1,
      spaceBetween: 12,
      pagination: { el: this.paginationEl().nativeElement, clickable: true },
      a11y: { enabled: true },
    });
  }
}
