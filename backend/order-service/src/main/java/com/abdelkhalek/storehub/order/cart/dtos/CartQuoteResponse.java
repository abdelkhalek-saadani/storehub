package com.abdelkhalek.storehub.order.cart.dtos;

import java.math.BigDecimal;
import java.util.List;

public record CartQuoteResponse(
        List<CartItemResponse> items,
        BigDecimal subtotal,
        BigDecimal total
) {
    public static CartQuoteResponse empty() {
        return new CartQuoteResponse(List.of(), BigDecimal.ZERO, BigDecimal.ZERO);
    }
}