package com.abdelkhalek.storehub.order.order.event;

import java.time.Instant;
import java.util.UUID;

public record PaymentStatusUpdateEvent(
        UUID orderId,
        UUID paymentId,
        PaymentStatus newStatus,
        Instant timestamp
){
    public enum PaymentStatus {
        CREATED,
        CAPTURED,
        CANCELLED,
        REFUNDED,
        APPROVED,
        AUTHORIZED
    }
}
