package com.abdelkhalek.storehub.catalog.inventory.exception;

import java.util.UUID;

public class InsufficientStockException extends RuntimeException {

    public InsufficientStockException(UUID productId, int requested, int available) {
        super("Insufficient stock for product " + productId
                + ": trying to subtract " + requested + "(requested), from " + available + "(available)");
    }
}
