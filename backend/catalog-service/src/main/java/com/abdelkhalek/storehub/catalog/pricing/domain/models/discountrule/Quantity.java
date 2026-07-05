package com.abdelkhalek.storehub.catalog.pricing.domain.models;

import com.abdelkhalek.storehub.catalog.pricing.DiscountRule;

import java.math.BigDecimal;

public record Quantity(int minimumQuantity, BigDecimal percentage) implements DiscountRule {}