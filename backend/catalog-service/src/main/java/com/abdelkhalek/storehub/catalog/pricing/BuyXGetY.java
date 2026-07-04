package com.abdelkhalek.storehub.catalog.pricing;

public record BuyXGetY(int buyQty, int getQty) implements DiscountRule {}