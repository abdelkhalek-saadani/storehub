package com.abdelkhalek.storehub.catalog.pricing.domain.models.discountrule;

import java.math.BigDecimal;

public record PercentageOff(BigDecimal percent) implements DiscountRule {}