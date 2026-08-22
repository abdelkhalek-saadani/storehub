import { ComponentFixture, TestBed } from '@angular/core/testing';
import { OffersMasonry } from './offers-masonry';
import { Offer } from '@shared/models/Offer';
import { provideZonelessChangeDetection } from '@angular/core';
import { provideRouter } from '@angular/router';

function buildOffer(overrides: Partial<Offer> = {}): Offer {
  return {
    id: 'offer-1',
    name: 'Summer Sale',
    slug: 'summer-sale',
    description: 'Up to 50% off',
    imageUrl: 'offer.png',
    ...overrides,
  } as Offer;
}

describe('OffersMasonry (smoke test)', () => {
  let fixture: ComponentFixture<OffersMasonry>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [OffersMasonry],
      providers: [provideZonelessChangeDetection(), provideRouter([])],
    });
    fixture = TestBed.createComponent(OffersMasonry);
  });

  it('renders without throwing and shows one link per offer', async () => {
    fixture.componentRef.setInput('offers', [buildOffer({ id: 'a' }), buildOffer({ id: 'b' })]);
    await fixture.whenStable();

    const links = fixture.nativeElement.querySelectorAll('a');
    expect(links.length).toBe(2);
  });

  it('renders nothing when offers is empty', async () => {
    fixture.componentRef.setInput('offers', []);

    await fixture.whenStable();

    expect(fixture.nativeElement.querySelectorAll('a').length).toBe(0);
  });
});
