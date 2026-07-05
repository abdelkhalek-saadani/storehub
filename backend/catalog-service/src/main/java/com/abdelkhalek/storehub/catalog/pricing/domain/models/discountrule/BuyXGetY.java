package com.abdelkhalek.storehub.catalog.pricing.domain.models.discountrule;

public record BuyXGetY(int requiredQty, int freeQty) implements DiscountRule {}