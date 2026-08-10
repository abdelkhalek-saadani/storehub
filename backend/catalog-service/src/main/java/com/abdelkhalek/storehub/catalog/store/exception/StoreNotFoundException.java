package com.abdelkhalek.storehub.catalog.store.exception;

public class StoreNotFoundException extends RuntimeException {
    public StoreNotFoundException(String message) {
        super(message);
    }
}
