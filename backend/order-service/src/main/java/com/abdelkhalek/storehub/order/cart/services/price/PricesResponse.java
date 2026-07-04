package com.abdelkhalek.storehub.order.cart.services.price;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
public class PricesResponse {
    List<PriceItemResponse> items;
    BigDecimal originalTotal;
    BigDecimal finalTotal;
    BigDecimal totalDiscount;

    public static PricesResponse empty() {
        return new PricesResponse(List.of(), BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);
    }

}
