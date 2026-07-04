package com.abdelkhalek.storehub.catalog.pricing;

import java.math.BigDecimal;

public record FixedAmountOff(BigDecimal amount) implements DiscountRule {}