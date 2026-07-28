package com.abdelkhalek.storehub.order.order.dto;

import com.abdelkhalek.storehub.order.order.models.Order;

import java.util.UUID;

public record OrderCreatedResponse(
        UUID orderId,
        UUID paymentId,
        String paymentApprovalUrl
) {
    public static OrderCreatedResponse from(Order order) {
        return new OrderCreatedResponse(order.getId(), order.getPaymentId(), order.getPaymentApprovalLink());
    }
}
