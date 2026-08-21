import { Component, provideZonelessChangeDetection } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { of, Subject } from 'rxjs';
import { CheckoutForm } from './checkout-form';
import { CatalogApi } from '@shared/service/catalog-api';
import { CheckoutFormService } from '../checkout-form.service';
import { OrderApi } from '@shared/service/order-api';
import { CartStore } from '../../cart/cart-store';
import { PhoneInput } from '../phone-input/phone-input';
import { DeliveryAddressForm } from '../delivery-address-form/delivery-address-form';
import { DateAndDay } from '@shared/models/DateAndDay';
import { DayOfWeek, LocalDate } from '@js-joda/core';
import IntlTelInput from '@intl-tel-input/angular';

@Component({ selector: 'app-phone-input', template: '', standalone: true })
class PhoneInputStub {}

@Component({ selector: 'app-delivery-address-form', template: '', standalone: true })
class DeliveryAddressFormStub {}

describe('CheckoutForm', () => {
  let component: CheckoutForm;
  let fixture: ComponentFixture<CheckoutForm>;
  let catalogApiSpy: jasmine.SpyObj<CatalogApi>;
  let daysSubject: Subject<DateAndDay[]>;

  beforeEach(async () => {
    daysSubject = new Subject<DateAndDay[]>();
    catalogApiSpy = jasmine.createSpyObj('CatalogApi', ['getDays', 'getSlots']);
    catalogApiSpy.getDays.and.returnValue(daysSubject.asObservable());
    catalogApiSpy.getSlots.and.returnValue(of([]));

    TestBed.configureTestingModule({
      imports: [CheckoutForm],
      providers: [
        provideZonelessChangeDetection(),
        { provide: CatalogApi, useValue: catalogApiSpy },
        CheckoutFormService,
        { provide: OrderApi, useValue: jasmine.createSpyObj('OrderApi', ['placeOrder']) },
        {
          provide: CartStore,
          useValue: { cartId: () => 'cart-1', finalTotal: () => 0, items: () => [] },
        },
      ],
    });

    TestBed.overrideComponent(CheckoutForm, {
      remove: { imports: [PhoneInput, DeliveryAddressForm] },
      add: { imports: [PhoneInputStub, DeliveryAddressFormStub] },
    });

    fixture = TestBed.createComponent(CheckoutForm);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });
  it('should create', async () => {
    expect(component).toBeTruthy();
  });

  describe('week / deliveryDay auto-select', () => {
    it('auto-selects the first day once days load, if none is selected yet', async () => {
      daysSubject.next([
        { date: LocalDate.of(2026, 8, 20), day: DayOfWeek.THURSDAY },
        { date: LocalDate.of(2026, 8, 21), day: DayOfWeek.FRIDAY },
      ]);
      await fixture.whenStable();

      expect(component.checkoutForm.controls.deliveryDay.value).toEqual(LocalDate.of(2026, 8, 20));
      expect(component.week().length).toBe(2);
    });

    it('does not override an already-selected day', async () => {
      component.checkoutForm.controls.deliveryDay.setValue(LocalDate.of(2026, 8, 22));

      daysSubject.next([{ date: LocalDate.of(2026, 8, 20), day: DayOfWeek.THURSDAY }]);
      await fixture.whenStable();

      expect(component.checkoutForm.controls.deliveryDay.value).toEqual(LocalDate.of(2026, 8, 22));
    });
  });

  describe('slotsResult', () => {
    it('does not fetch slots while no date is selected', async () => {
      expect(catalogApiSpy.getSlots).not.toHaveBeenCalled();
    });

    it('fetches slots for the selected date and auto-selects the first slot', async () => {
      catalogApiSpy.getSlots.and.returnValue(
        of([
          { slotId: 'slot-1', slotLabel: '9-10am' },
          { slotId: 'slot-2', slotLabel: '10-11am' },
        ]),
      );

      component.checkoutForm.controls.deliveryDay.setValue(LocalDate.of(2026, 8, 20));
      await fixture.whenStable();

      expect(catalogApiSpy.getSlots).toHaveBeenCalledWith(LocalDate.of(2026, 8, 20));
      expect(component.checkoutForm.controls.slotId.value).toBe('slot-1');
    });

    it('sets slotId to an empty string when no slots are returned for the date', async () => {
      catalogApiSpy.getSlots.and.returnValue(of([]));

      component.checkoutForm.controls.deliveryDay.setValue(LocalDate.of(2026, 8, 20));
      await fixture.whenStable();

      expect(component.checkoutForm.controls.slotId.value).toBe('');
    });
  });

  describe('isInvalid / isTouched', () => {
    it('isInvalid is true only once the control is both invalid and touched', async () => {
      component.checkoutForm.controls.firstName.setValue('');
      expect(component.isInvalid('firstName')).toBeFalse(); // untouched

      component.checkoutForm.controls.firstName.markAsTouched();
      expect(component.isInvalid('firstName')).toBeTrue();
    });

    it('isTouched reflects control.touched', () => {
      expect(component.isTouched('email')).toBeFalse();
      component.checkoutForm.controls.email.markAsTouched();
      expect(component.isTouched('email')).toBeTrue();
    });
  });

  describe('template (integration) — email errors', () => {
    it('shows a required error when email is empty and touched', async () => {
      component.checkoutForm.controls.email.setValue('');
      component.checkoutForm.controls.email.markAsTouched();
      await fixture.whenStable();

      expect(fixture.nativeElement.textContent).toContain('Email is required.');
    });

    it('shows an invalid-email error when email is malformed and touched', async () => {
      component.checkoutForm.controls.email.setValue('not-an-email');
      component.checkoutForm.controls.email.markAsTouched();
      await fixture.whenStable();

      expect(fixture.nativeElement.textContent).toContain('Enter a valid email.');
    });

    it('shows no error before the field has been touched', async () => {
      component.checkoutForm.controls.email.setValue('');
      await fixture.whenStable();

      expect(fixture.nativeElement.textContent).not.toContain('Email is required.');
    });
  });
});
