import { ComponentFixture, TestBed } from '@angular/core/testing';
import { BehaviorSubject } from 'rxjs';
import { BreakpointObserver, BreakpointState } from '@angular/cdk/layout';
import { OrderTracking } from './order-tracking';
import { OrderStatusDto } from '@shared/service/order-api';
import { provideZonelessChangeDetection } from '@angular/core';

function buildStatus(code: string, label = code): OrderStatusDto {
  return { code, label } as OrderStatusDto;
}

describe('OrderTracking', () => {
  let fixture: ComponentFixture<OrderTracking>;
  let component: OrderTracking;
  let breakpointSubject: BehaviorSubject<BreakpointState>;

  beforeEach(async () => {
    breakpointSubject = new BehaviorSubject<BreakpointState>({ matches: false, breakpoints: {} });

    TestBed.configureTestingModule({
      imports: [OrderTracking],
      providers: [
        provideZonelessChangeDetection(),
        {
          provide: BreakpointObserver,
          useValue: { observe: () => breakpointSubject.asObservable() },
        },
      ],
    });

    fixture = TestBed.createComponent(OrderTracking);
    component = fixture.componentInstance;
  });

  async function setInputs(
    status: OrderStatusDto,
    createdAt: Date = new Date(),
    orderArriveIn: string | null = null,
  ) {
    fixture.componentRef.setInput('status', status);
    fixture.componentRef.setInput('createdAt', createdAt);
    fixture.componentRef.setInput('orderArriveIn', orderArriveIn);
    await fixture.whenStable();
  }

  describe('isOrderFailed', () => {
    it('is true for PAYMENT_FAILED, PAYMENT_VOIDED, PAYMENT_REFUNDED', async () => {
      for (const code of ['PAYMENT_FAILED', 'PAYMENT_VOIDED', 'PAYMENT_REFUNDED']) {
        await setInputs(buildStatus(code));
        expect(component.isOrderFailed()).toBeTrue();
      }
    });

    it('is false for a normal status like SHIPPED', async () => {
      await setInputs(buildStatus('SHIPPED'));
      expect(component.isOrderFailed()).toBeFalse();
    });
  });

  describe('isFirstStepChecked', () => {
    it('is true for PAYMENT_AUTHORIZED, PAYMENT_CAPTURED, SHIPPED, DELIVERED', async () => {
      for (const code of ['PAYMENT_AUTHORIZED', 'PAYMENT_CAPTURED', 'SHIPPED', 'DELIVERED']) {
        await setInputs(buildStatus(code));
        expect(component.isFirstStepChecked()).toBeTrue();
      }
    });

    it('is false for CREATED', async () => {
      await setInputs(buildStatus('CREATED'));
      expect(component.isFirstStepChecked()).toBeFalse();
    });
  });

  describe('isSecondStepChecked', () => {
    it('is true for SHIPPED', async () => {
      await setInputs(buildStatus('SHIPPED'));
      expect(component.isSecondStepChecked()).toBeTrue();
    });

    it('is true for DELIVERED (delivered implies shipped)', async () => {
      await setInputs(buildStatus('DELIVERED'));
      expect(component.isSecondStepChecked()).toBeTrue();
    });

    it('is false for CREATED', async () => {
      await setInputs(buildStatus('CREATED'));
      expect(component.isSecondStepChecked()).toBeFalse();
    });
  });

  describe('isThirdStepChecked', () => {
    it('is true only for DELIVERED', async () => {
      await setInputs(buildStatus('DELIVERED'));
      expect(component.isThirdStepChecked()).toBeTrue();
    });

    it('is false for SHIPPED', async () => {
      await setInputs(buildStatus('SHIPPED'));
      expect(component.isThirdStepChecked()).toBeFalse();
    });
  });

  describe('isMdDevice', () => {
    it('reflects BreakpointObserver.observe(Breakpoints.md)', async () => {
      await setInputs(buildStatus('CREATED'));
      expect(component.isMdDevice()).toBeFalse();

      breakpointSubject.next({ matches: true, breakpoints: {} });
      await fixture.whenStable();

      expect(component.isMdDevice()).toBeTrue();
    });
  });

  // This is the pattern to reuse for SSE-driven live updates elsewhere: the
  // parent normally derives `status` from a stream (The SSE-backed
  // resource); here we simulate that by pushing new input values over time
  // and asserting the DOM reflects each one, without touching real SSE/EventSource.
  describe('live status updates (template)', () => {
    it('updates the rendered label and progress bars as the status input changes over time', async () => {
      await setInputs(buildStatus('CREATED', 'Order Placed'));
      let compiled = fixture.nativeElement as HTMLElement;
      expect(compiled.textContent).toContain('Order Placed');
      expect(component.isFirstStepChecked()).toBeFalse();

      await setInputs(buildStatus('PAYMENT_AUTHORIZED', 'Paid'));
      compiled = fixture.nativeElement as HTMLElement;
      expect(compiled.textContent).toContain('Paid');
      expect(component.isFirstStepChecked()).toBeTrue();
      expect(component.isSecondStepChecked()).toBeFalse();
      expect(component.isThirdStepChecked()).toBeFalse();

      await setInputs(buildStatus('SHIPPED', 'Shipped'));
      compiled = fixture.nativeElement as HTMLElement;
      expect(compiled.textContent).toContain('Shipped');
      expect(component.isFirstStepChecked()).toBeTrue();
      expect(component.isSecondStepChecked()).toBeTrue();
      expect(component.isThirdStepChecked()).toBeFalse();

      await setInputs(buildStatus('DELIVERED', 'Delivered'));
      compiled = fixture.nativeElement as HTMLElement;
      expect(compiled.textContent).toContain('Delivered');
      expect(component.isFirstStepChecked()).toBeTrue();
      expect(component.isSecondStepChecked()).toBeTrue();
      expect(component.isThirdStepChecked()).toBeTrue();
    });
  });
});
