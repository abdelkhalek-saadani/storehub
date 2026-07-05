package com.abdelkhalek.storehub.catalog.pricing.domain.models.discountrule;

import java.math.BigDecimal;

public record FixedAmountOff(BigDecimal amount) implements DiscountRule {}