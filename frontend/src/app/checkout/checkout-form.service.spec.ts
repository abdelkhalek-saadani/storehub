import { TestBed } from '@angular/core/testing';
import { of, throwError } from 'rxjs';
import { CheckoutFormService } from './checkout-form.service';
import { OrderApi } from '@shared/service/order-api';
import { CartStore } from '../cart/cart-store';
import { provideZonelessChangeDetection } from '@angular/core';
import { NavigationService } from './navigation.service';

describe('CheckoutFormService', () => {
  let service: CheckoutFormService;
  let orderApiSpy: jasmine.SpyObj<OrderApi>;
  let cartStoreMock: { cartId: jasmine.Spy };
  let navigationServiceSpy: jasmine.SpyObj<NavigationService>;

  beforeEach(() => {
    orderApiSpy = jasmine.createSpyObj('OrderApi', ['placeOrder']);
    cartStoreMock = { cartId: jasmine.createSpy('cartId').and.returnValue('cart-123') };
    navigationServiceSpy = jasmine.createSpyObj('NavigationService', ['redirectTo']);

    TestBed.configureTestingModule({
      providers: [
        provideZonelessChangeDetection(),
        CheckoutFormService,
        { provide: OrderApi, useValue: orderApiSpy },
        { provide: CartStore, useValue: cartStoreMock },
        { provide: NavigationService, useValue: navigationServiceSpy },
      ],
    });

    service = TestBed.inject(CheckoutFormService);
  });

  // The default form has an empty slotId (required), so it's invalid until we set one.
  function makeFormValid() {
    service.form.controls.slotId.setValue('slot-1');
  }

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  describe('submit, invalid form', () => {
    it('marks all fields touched and does not call placeOrder when the form is invalid', () => {
      spyOn(service.form, 'markAllAsTouched').and.callThrough();

      service.submit(); // slotId still empty by default -> invalid

      expect(service.form.markAllAsTouched).toHaveBeenCalled();
      expect(orderApiSpy.placeOrder).not.toHaveBeenCalled();
    });
  });

  describe('submit,  already submitting', () => {
    it('does nothing when submitting() is already true', () => {
      makeFormValid();
      service.submitting.set(true);

      service.submit();

      expect(orderApiSpy.placeOrder).not.toHaveBeenCalled();
    });
  });

  describe('submit, missing cartId', () => {
    it('sets a submitError and does not call placeOrder', () => {
      makeFormValid();
      cartStoreMock.cartId.and.returnValue(null);

      service.submit();

      expect(service.submitError()).toBe(
        'Your cart could not be found. Please refresh and try again.',
      );
      expect(orderApiSpy.placeOrder).not.toHaveBeenCalled();
    });
  });

  describe('submit, happy path', () => {
    it('calls placeOrder with the idempotency key and correctly mapped body', () => {
      makeFormValid();
      orderApiSpy.placeOrder.and.returnValue(
        of({
          paymentApprovalUrl: 'https://pay.example.com',
          orderId: 'orderUuid',
          paymentId: ' paymentUuid',
        }),
      );

      service.submit();

      expect(orderApiSpy.placeOrder).toHaveBeenCalledWith(
        service.idempotencyKey,
        jasmine.objectContaining({
          cartId: 'cart-123',
          slotId: 'slot-1',
          firstName: 'Abdelkhalek',
          lastName: 'Saadani',
          email: 'abdelkhalek@gmail.com',
          deliveryAddress: jasmine.objectContaining({
            type: 'home',
            street: 'zuhur street',
            city: 'zayatine city',
            apartmentNumber: '22',
            zipCode: '8030',
            deliveryInstructions: 'Jawk behi',
          }),
        }),
      );
    });

    it('falls back to empty strings for optional delivery address fields when null', () => {
      makeFormValid();
      service.form.controls.deliveryAddress.controls.apartmentNumber.setValue(null);
      service.form.controls.deliveryAddress.controls.zipCode.setValue(null);
      service.form.controls.deliveryAddress.controls.deliveryInstructions.setValue(null);
      orderApiSpy.placeOrder.and.returnValue(
        of({
          paymentApprovalUrl: 'https://pay.example.com',
          orderId: 'orderUuid',
          paymentId: ' paymentUuid',
        }),
      );

      service.submit();

      expect(orderApiSpy.placeOrder).toHaveBeenCalledWith(
        service.idempotencyKey,
        jasmine.objectContaining({
          deliveryAddress: jasmine.objectContaining({
            apartmentNumber: '',
            zipCode: '',
            deliveryInstructions: '',
          }),
        }),
      );
    });

    it('redirects to paymentApprovalUrl on success', () => {
      makeFormValid();
      orderApiSpy.placeOrder.and.returnValue(
        of({
          paymentApprovalUrl: 'https://pay.example.com',
          orderId: 'orderUuid',
          paymentId: ' paymentUuid',
        }),
      );
      navigationServiceSpy.redirectTo.and.stub();

      service.submit();

      expect(navigationServiceSpy.redirectTo).toHaveBeenCalledWith('https://pay.example.com');
    });

    it('sets submitting to false and submitError on API error', () => {
      makeFormValid();
      orderApiSpy.placeOrder.and.returnValue(throwError(() => new Error('network error')));

      service.submit();

      expect(service.submitting()).toBeFalse();
      expect(service.submitError()).toBe('Something went wrong. Please try again.');
    });
  });
});
