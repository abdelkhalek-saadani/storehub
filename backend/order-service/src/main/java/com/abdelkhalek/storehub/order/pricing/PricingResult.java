package com.abdelkhalek.storehub.order.pricing;

import com.abdelkhalek.storehub.order.pricing.domain.models.Item;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
public class PricingResult {
    List<Item> items;
    BigDecimal originalTotal;
    BigDecimal finalTotal;
    BigDecimal totalDiscount;




}