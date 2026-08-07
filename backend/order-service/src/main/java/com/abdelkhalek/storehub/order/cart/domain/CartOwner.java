package com.abdelkhalek.storehub.order.cart.domain;

import java.util.UUID;

public record CartOwner(UUID userId, UUID guestId) {
    public static CartOwner ofUser(UUID id) { return new CartOwner(id, null); }
    public static CartOwner ofGuest(UUID id) { return new CartOwner(null, id); }
    public boolean isGuest() { return userId == null; }
}