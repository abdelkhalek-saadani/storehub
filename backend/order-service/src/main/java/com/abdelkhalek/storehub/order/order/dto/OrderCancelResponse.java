package com.abdelkhalek.storehub.order.order.dto;

import java.util.UUID;

public record OrderCancelResponse(
        UUID orderId,
        UUID paymentId,
        OrderStatusDto orderStatus,
        String message
) {
}
