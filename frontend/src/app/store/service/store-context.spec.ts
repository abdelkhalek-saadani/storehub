import { StoreContext } from './store-context';
import { Store } from '@shared/models/Store';

function buildStore(overrides: Partial<Store> = {}): Store {
  return {
    storeId: 'store-1',
    storeSlug: 'super-mart',
    storeName: 'Super Mart',
    ...overrides,
  };
}

describe('StoreContext', () => {
  let service: StoreContext;

  beforeEach(() => {
    service = new StoreContext();
    localStorage.clear();
  });

  afterEach(() => {
    localStorage.clear();
  });

  describe('setStore', () => {
    it('updates the storeId/storeSlug/storeName signals', () => {
      service.setStore(buildStore());

      expect(service.storeId()).toBe('store-1');
      expect(service.storeSlug()).toBe('super-mart');
      expect(service.storeName()).toBe('Super Mart');
    });

    it('persists the store to localStorage', () => {
      service.setStore(buildStore());

      expect(service.getCurrentStore()).toEqual({
        id: 'store-1',
        slug: 'super-mart',
        name: 'Super Mart',
      });
    });
  });

  describe('getCurrentStore', () => {
    it('returns null when nothing is stored', () => {
      expect(service.getCurrentStore()).toBeNull();
    });

    it('returns null and clears storage when data is corrupted', () => {
      localStorage.setItem('current_store', '{invalid json');
      const removeSpy = spyOn(localStorage, 'removeItem').and.callThrough();

      const result = service.getCurrentStore();

      expect(result).toBeNull();
      expect(removeSpy).toHaveBeenCalledWith('current_store');
    });
  });

  describe('clearCurrentStore', () => {
    it('clears the current store from localStorage', () => {
      service.setStore(buildStore());
      expect(service.getCurrentStore()).not.toBeNull();
      service.clearCurrentStore();

      expect(service.getCurrentStore()).toBeNull();
    });
  });
});
