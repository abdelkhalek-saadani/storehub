package com.abdelkhalek.storehub.catalog.pricing.dto;

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

}
