package com.abdelkhalek.storehub.catalog.inventory.exception;

public class NoReservationsForSuchOrderException extends RuntimeException {
    public NoReservationsForSuchOrderException(String message) {
        super(message);
    }
}
