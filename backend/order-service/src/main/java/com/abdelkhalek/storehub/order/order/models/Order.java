package com.abdelkhalek.storehub.order.order.models;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
public class Order {

    UUID id;
    UUID userId;
    UUID storeId;
    BigDecimal originalTotal;
    BigDecimal finalTotal;
    BigDecimal totalDiscount;
    List<OrderItem> items;

    @Builder.Default
    LocalDateTime createdAt = LocalDateTime.now();
    LocalDateTime updatedAt;

    String deliveryAddress;
    String billingAddress;

    UUID slotId;

    BigDecimal deliveryFee = BigDecimal.TEN;

    UUID slotRetainId;
    List<UUID> inventoryRetainIds;

    OrderStatus status;

    UUID paymentId;
    String paymentOrderId;

    String paymentApprovalLink;

    UUID idempotencyKey;
}
