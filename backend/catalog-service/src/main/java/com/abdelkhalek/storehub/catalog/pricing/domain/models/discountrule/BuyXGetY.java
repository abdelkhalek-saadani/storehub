package com.abdelkhalek.storehub.catalog.pricing.domain.models;

import com.abdelkhalek.storehub.catalog.pricing.DiscountRule;

public record BuyXGetY(int requiredQty, int freeQty) implements DiscountRule {}