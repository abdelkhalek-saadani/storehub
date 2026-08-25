import { PendingStoreStorage, PendingStore, STORAGE_KEY } from './pending-store-storage';

describe('PendingStoreStorage', () => {
  let service: PendingStoreStorage;

  beforeEach(() => {
    service = new PendingStoreStorage();
    sessionStorage.clear();
  });

  afterEach(() => {
    sessionStorage.clear();
  });

  it('should save and retrieve a pending store', () => {
    const store: PendingStore = { name: 'Super Mart', description: 'desc', address: 'addr' };
    service.savePendingStore(store);
    expect(service.getPendingStore()).toEqual(store);
  });

  it('should return null when no pending store exists', () => {
    expect(service.getPendingStore()).toBeNull();
  });

  it('should return null and clear storage when data is corrupted', () => {
    sessionStorage.setItem(STORAGE_KEY, '{invalid json');
    const removeSpy = spyOn(sessionStorage, 'removeItem').and.callThrough();

    spyOn(console, 'error');
    const result = service.getPendingStore();

    expect(result).toBeNull();
    expect(removeSpy).toHaveBeenCalledWith(STORAGE_KEY);
  });

  it('should clear the pending store', () => {
    service.savePendingStore({ name: 'a', description: 'b', address: 'c' });
    service.clearPendingStore();
    expect(service.getPendingStore()).toBeNull();
  });
});
