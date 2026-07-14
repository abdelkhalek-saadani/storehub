import { Component, input } from '@angular/core';
import { RouterLink } from '@angular/router';
import { Offer } from '../../shared/models/Offer';
import { NgClass } from '@angular/common';

@Component({
  selector: 'app-offers-masonry',
  standalone: true,
  imports: [RouterLink, NgClass],
  template: `
    <section class="grid grid-cols-27 grid-rows-[248px_248px] gap-3" aria-label="Special offers">
      @for (offer of offers(); track offer.id) {
        <a
          routerLink="../products"
          [queryParams]="{ saleEvent: offer.slug }"
          [attr.aria-label]="offer.description"
          [ngClass]="['relative block rounded-lg overflow-hidden no-underline ', columns[$index]]"
        >
          <img
            class="h-full w-full object-cover transition-transform duration-400 ease-in-out hover:scale-[1.04]"
            [src]="offer.imageUrl"
            [alt]="offer.description"
            loading="lazy"
          />
          <span class="absolute bottom-4 left-4 text-white text-base font-medium">{{
            offer.name
          }}</span>
        </a>
      }
    </section>
  `,
  styles: [``],
})
export class OffersMasonry {
  offers = input.required<Offer[]>();

  // This is used to make the masonry layout by specifying a different col span for each grid cell
  columns: string[] = [
    'col-span-9',
    'col-span-12',
    'col-span-6',
    'col-span-6',
    'col-span-16',
    'col-span-5',
  ];
  protected readonly parseInt = parseInt;
}
