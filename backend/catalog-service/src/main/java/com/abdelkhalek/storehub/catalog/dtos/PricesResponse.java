package com.abdelkhalek.storehub.catalog.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PricesResponse {
    List<PriceItemResponse> items;
    BigDecimal originalTotal;
    BigDecimal finalTotal;
    BigDecimal totalDiscount;

    public static PricesResponse empty() {
        return new PricesResponse(List.of(), BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);
    }

}
