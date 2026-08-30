package com.abdelkhalek.storehub.order.shared.dto;

import java.math.BigDecimal;
import java.util.List;


public record PricesResponse(
        List<PriceItemResponse> items,
        BigDecimal originalTotal,
        BigDecimal finalTotal,
        BigDecimal totalDiscount
) {
    public static PricesResponse empty(){
        return new PricesResponse(List.of(),BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO);
    }
}
