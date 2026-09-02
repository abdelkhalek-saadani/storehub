package com.abdelkhalek.storehub.order.order.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record OrderItemDto(
        UUID itemId,

        String productName,
        String productImageUrl,

        UUID productId,

        int quantity,

        BigDecimal unitPrice,
        BigDecimal originalLineTotal,
        BigDecimal discountAmount,
        BigDecimal finalLineTotal,
        String appliedOfferLabel

        ) {
}
