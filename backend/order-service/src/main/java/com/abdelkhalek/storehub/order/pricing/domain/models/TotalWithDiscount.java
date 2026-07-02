package com.abdelkhalek.storehub.order.pricing.domain.models;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class TotalWithDiscount {

    private BigDecimal total;
    private BigDecimal discount;

}
