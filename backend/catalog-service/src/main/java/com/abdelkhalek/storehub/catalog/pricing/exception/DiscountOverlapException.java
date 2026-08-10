package com.abdelkhalek.storehub.catalog.pricing.exception;


import java.time.Instant;
import java.util.UUID;

public class DiscountOverlapException extends RuntimeException {
    public DiscountOverlapException(UUID productId, Instant startsAt, Instant endsAt) {
        super("Product " + productId + " already has an active discount overlapping "
                + startsAt + " - " + endsAt);
    }
}