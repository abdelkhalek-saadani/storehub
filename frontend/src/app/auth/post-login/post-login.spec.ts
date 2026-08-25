import { ComponentFixture, TestBed } from '@angular/core/testing';
import { of, throwError } from 'rxjs';
import { Router } from '@angular/router';
import { PostLogin } from './post-login';
import { StoreApi } from '../../store/service/store-api';
import { PendingStoreStorage } from '../pending-store-storage';
import { provideZonelessChangeDetection } from '@angular/core';

describe('PostLogin', () => {
  let component: PostLogin;
  let fixture: ComponentFixture<PostLogin>;
  let storeApiSpy: jasmine.SpyObj<StoreApi>;
  let pendingStoreStorageSpy: jasmine.SpyObj<PendingStoreStorage>;
  let routerSpy: jasmine.SpyObj<Router>;

  beforeEach(() => {
    storeApiSpy = jasmine.createSpyObj('StoreApi', ['createStore']);
    pendingStoreStorageSpy = jasmine.createSpyObj('PendingStoreStorage', [
      'getPendingStore',
      'clearPendingStore',
    ]);
    routerSpy = jasmine.createSpyObj('Router', ['navigate']);

    TestBed.configureTestingModule({
      imports: [PostLogin],
      providers: [
        provideZonelessChangeDetection(),
        { provide: StoreApi, useValue: storeApiSpy },
        { provide: PendingStoreStorage, useValue: pendingStoreStorageSpy },
        { provide: Router, useValue: routerSpy },
      ],
    });

    fixture = TestBed.createComponent(PostLogin);
    component = fixture.componentInstance;
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('navigates to /dev without creating a store when none is pending', async () => {
    pendingStoreStorageSpy.getPendingStore.and.returnValue(null);

    await component.ngOnInit();

    expect(storeApiSpy.createStore).not.toHaveBeenCalled();
    expect(pendingStoreStorageSpy.clearPendingStore).not.toHaveBeenCalled();
    expect(routerSpy.navigate).toHaveBeenCalledWith(['/']);
  });

  it('creates the pending store, clears it, then navigates', async () => {
    const pending = { name: 'Store', description: 'desc', address: 'addr' };
    pendingStoreStorageSpy.getPendingStore.and.returnValue(pending);
    storeApiSpy.createStore.and.returnValue(of({} as any));

    await component.ngOnInit();

    expect(storeApiSpy.createStore).toHaveBeenCalledWith(pending);
    expect(pendingStoreStorageSpy.clearPendingStore).toHaveBeenCalled();
    expect(routerSpy.navigate).toHaveBeenCalledWith(['/']);
  });

  it('still clears storage and navigates even if store creation fails', async () => {
    const pending = { name: 'Store', description: 'desc', address: 'addr' };
    pendingStoreStorageSpy.getPendingStore.and.returnValue(pending);
    storeApiSpy.createStore.and.returnValue(throwError(() => new Error('API error')));
    spyOn(console, 'error'); // silence expected error log

    await component.ngOnInit();

    expect(pendingStoreStorageSpy.clearPendingStore).toHaveBeenCalled();
    expect(routerSpy.navigate).toHaveBeenCalledWith(['/']);
  });
});
