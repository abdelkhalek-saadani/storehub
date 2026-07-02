package com.abdelkhalek.storehub.order.cart.dtos;

import java.math.BigDecimal;
import java.util.UUID;

public record CartItemResponse(
        UUID itemId,
        UUID productId,
        int quantity,
        BigDecimal unitPrice,
        BigDecimal originalLineTotal,
        BigDecimal discountAmount,
        BigDecimal finalLineTotal,
        String appliedOfferLabel
) {
}