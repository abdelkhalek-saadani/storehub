package com.abdelkhalek.storehub.order.shared.model;

import java.util.UUID;

public record ServiceResult<T>(T body, UUID guestId) {
    public boolean isGuest() {
        return guestId != null;
    }
    public static <T> ServiceResult<T> forGuest(T body, UUID guestId) {
        return new ServiceResult<>(body, guestId);
    }
    public static <T> ServiceResult<T> forUser(T body) {
        return new ServiceResult<>(body, null);
    }
}