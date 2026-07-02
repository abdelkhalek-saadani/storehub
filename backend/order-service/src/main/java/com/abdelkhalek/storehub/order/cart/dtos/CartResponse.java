package com.abdelkhalek.storehub.order.cart.dtos;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record CartResponse(
        UUID cartId,
        List<CartItemResponse> items,
        BigDecimal originalTotal,
        BigDecimal finalTotal,
        BigDecimal totalDiscount,
        UUID storeId

) {
}