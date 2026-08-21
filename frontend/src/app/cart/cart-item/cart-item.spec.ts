import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideZonelessChangeDetection, signal, WritableSignal } from '@angular/core';
import { CartItem } from './cart-item';
import { CartStore } from '../cart-store';
import { CartItemResponse } from '../model/cart-response';

function buildItem(overrides: Partial<CartItemResponse> = {}): CartItemResponse {
  return {
    itemId: 'item-1',
    productId: 'prod-1',
    productName: 'Milk',
    productImageUrl: '',
    quantity: 2,
    unitPrice: 3.5,
    originalLineTotal: 7,
    discountAmount: 0,
    finalLineTotal: 7,
    appliedOfferLabel: '',
    ...overrides,
  };
}

describe('CartItem', () => {
  let component: CartItem;
  let fixture: ComponentFixture<CartItem>;
  let storeMock: {
    items: WritableSignal<CartItemResponse[]>;
    upsertItems: jasmine.Spy;
  };

  beforeEach(() => {
    storeMock = {
      items: signal<CartItemResponse[]>([]),
      upsertItems: jasmine.createSpy('upsertItems'),
    };

    TestBed.configureTestingModule({
      imports: [CartItem],
      providers: [provideZonelessChangeDetection(), { provide: CartStore, useValue: storeMock }],
    });

    fixture = TestBed.createComponent(CartItem);
    component = fixture.componentInstance;
  });

  async function setItem(item: CartItemResponse) {
    fixture.componentRef.setInput('item', item);
    await fixture.whenStable();
  }

  it('should create', async () => {
    await setItem(buildItem());
    expect(component).toBeTruthy();
  });

  describe('onIncrementQty', () => {
    it('increments the quantity of an existing item in the store', async () => {
      await setItem(buildItem({ productId: 'prod-1', quantity: 2 }));

      component.onIncrementQty();

      expect(storeMock.upsertItems).toHaveBeenCalledWith([{ productId: 'prod-1', quantity: 3 }]);
    });
  });

  describe('onDecrementQty', () => {
    it('decrements the current item quantity by 1', async () => {
      await setItem(buildItem({ productId: 'prod-1', quantity: 3 }));

      component.onDecrementQty();

      expect(storeMock.upsertItems).toHaveBeenCalledWith([{ productId: 'prod-1', quantity: 2 }]);
    });
  });

  describe('onRemoveItem', () => {
    it('sets quantity to 0', async () => {
      await setItem(buildItem({ productId: 'prod-1' }));

      component.onRemoveItem('prod-1');

      expect(storeMock.upsertItems).toHaveBeenCalledWith([{ productId: 'prod-1', quantity: 0 }]);
    });
  });

  describe('template (integration)', () => {
    it('clicking Remove calls onRemoveItem with the productId', async () => {
      await setItem(buildItem({ productId: 'prod-1' }));
      spyOn(component, 'onRemoveItem');

      const buttons: HTMLButtonElement[] = Array.from(
        fixture.nativeElement.querySelectorAll('.sidecart-button'),
      );
      const removeBtn = buttons.find((b) => b.textContent?.trim().includes('Remove'));
      removeBtn!.click();
      await fixture.whenStable();

      expect(component.onRemoveItem).toHaveBeenCalledWith('prod-1');
    });

    it('displays the quantity from the item input', async () => {
      await setItem(buildItem({ productId: 'prod-1', quantity: 4 }));

      const compiled = fixture.nativeElement as HTMLElement;
      expect(compiled.textContent).toContain('4');
    });
  });
});
