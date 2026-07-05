package com.abdelkhalek.storehub.catalog.pricing.domain.models.discountrule;

import java.math.BigDecimal;

public record Quantity(int minimumQuantity, BigDecimal percentage) implements DiscountRule {}