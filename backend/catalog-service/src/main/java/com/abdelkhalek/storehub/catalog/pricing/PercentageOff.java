package com.abdelkhalek.storehub.catalog.pricing;

import java.math.BigDecimal;

public record PercentageOff(BigDecimal percent) implements DiscountRule {}