package com.abdelkhalek.storehub.order.order.dto;

import java.util.UUID;

public record OrderCreatedResponse(
        UUID orderId,
        UUID paymentId,
        String paymentApprovalUrl
) {
}
