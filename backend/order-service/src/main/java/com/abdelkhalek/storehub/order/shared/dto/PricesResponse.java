package com.abdelkhalek.storehub.order.shared.dto;

import java.math.BigDecimal;
import java.util.List;


public record PricesResponse(
        List<PriceItemResponse> items,
        BigDecimal originalTotal,
        BigDecimal finalTotal,
        BigDecimal totalDiscount
) {
}
