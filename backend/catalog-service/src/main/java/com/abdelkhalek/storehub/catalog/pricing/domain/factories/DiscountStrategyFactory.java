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

    private static final Map<String, DiscountStrategy> strategyCache = new ConcurrentHashMap<>();

    public static synchronized DiscountStrategy getDiscountStrategy(DiscountWithProductIds discount) {
        String key = discount.getType().name().toLowerCase();
        DiscountStrategy cachedStrategy = strategyCache.get(key);
        if (cachedStrategy != null) {
            log.debug("Discount strategy already exists for discount id {}", discount);
            cachedStrategy.update(discount);
            log.debug("Cached discount strategy updated {} ", cachedStrategy);
            return cachedStrategy;
        }
        DiscountStrategy newStrategy;
        switch (discount.getRule()) {
            case PercentageOff ignored:
                newStrategy = new PercentageDiscount(discount);
                strategyCache.put(key, newStrategy);
                return newStrategy;
            case Quantity ignored:
                newStrategy = new QuantityDiscount(discount);
                strategyCache.put(key, newStrategy);
                return newStrategy;
            case BuyXGetY ignored:
                newStrategy = new BuyXGetYDiscount(discount);
                strategyCache.put(key, newStrategy);
                return newStrategy;
            default:
                throw new RuntimeException("No such discount strategy: " + discount.getId());
        }
    }

}
