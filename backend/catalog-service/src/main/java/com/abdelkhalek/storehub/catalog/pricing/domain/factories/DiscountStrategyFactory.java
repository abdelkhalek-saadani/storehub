package com.abdelkhalek.storehub.catalog.pricing.domain.factories;


import com.abdelkhalek.storehub.catalog.pricing.domain.models.discountrule.BuyXGetY;
import com.abdelkhalek.storehub.catalog.pricing.domain.models.DiscountWithProductIds;
import com.abdelkhalek.storehub.catalog.pricing.domain.models.discountrule.PercentageOff;
import com.abdelkhalek.storehub.catalog.pricing.domain.models.discountrule.Quantity;
import com.abdelkhalek.storehub.catalog.pricing.domain.strategies.BuyXGetYDiscount;
import com.abdelkhalek.storehub.catalog.pricing.domain.strategies.DiscountStrategy;
import com.abdelkhalek.storehub.catalog.pricing.domain.strategies.PercentageDiscount;
import com.abdelkhalek.storehub.catalog.pricing.domain.strategies.QuantityDiscount;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class DiscountStrategyFactory {

    public static synchronized DiscountStrategy getDiscountStrategy(DiscountWithProductIds discount) {
        return switch (discount.getRule()) {
            case PercentageOff ignored -> new PercentageDiscount(discount);
            case Quantity ignored -> new QuantityDiscount(discount);
            case BuyXGetY ignored -> new BuyXGetYDiscount(discount);
            default -> throw new RuntimeException("No such discount strategy: " + discount.getId());
        };
    }

}
