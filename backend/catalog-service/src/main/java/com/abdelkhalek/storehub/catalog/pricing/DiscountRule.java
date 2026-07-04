package com.abdelkhalek.storehub.catalog.pricing;


import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.math.BigDecimal;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "ruleType")
@JsonSubTypes({
        @JsonSubTypes.Type(value = PercentageOff.class, name = "PERCENTAGE_OFF"),
        @JsonSubTypes.Type(value = FixedAmountOff.class, name = "FIXED_AMOUNT_OFF"),
        @JsonSubTypes.Type(value = BuyXGetY.class, name = "BUY_X_GET_Y")
})
public sealed interface DiscountRule permits PercentageOff, FixedAmountOff, BuyXGetY {
}