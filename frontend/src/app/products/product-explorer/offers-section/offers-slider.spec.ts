import { ComponentFixture, TestBed } from '@angular/core/testing';
import { OffersSlider } from './offers-slider';
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
  };
}

describe('OffersSlider (smoke test)', () => {
  let fixture: ComponentFixture<OffersSlider>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [OffersSlider],
      providers: [provideZonelessChangeDetection(), provideRouter([])],
    });
    fixture = TestBed.createComponent(OffersSlider);
  });

  it('renders without throwing when Swiper initializes', async () => {
    fixture.componentRef.setInput('offers', [buildOffer({ id: 'a' }), buildOffer({ id: 'b' })]);

    expect(() => {
      fixture.detectChanges(); // force sync render to catch any exception Swiper throws on init
    }).not.toThrow();

    await fixture.whenStable();

    const slides = fixture.nativeElement.querySelectorAll('.swiper-slide');
    expect(slides.length).toBe(2);
  });
});
