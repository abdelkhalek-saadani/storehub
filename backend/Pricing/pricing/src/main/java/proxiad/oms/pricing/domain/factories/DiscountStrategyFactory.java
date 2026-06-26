package proxiad.oms.pricing.domain.factories;

import lombok.extern.slf4j.Slf4j;
import proxiad.oms.pricing.domain.models.Discount;
import proxiad.oms.pricing.domain.strategies.BuyXGetYDiscount;
import proxiad.oms.pricing.domain.strategies.DiscountStrategy;
import proxiad.oms.pricing.domain.strategies.PercentageDiscount;
import proxiad.oms.pricing.domain.strategies.QuantityDiscount;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class DiscountStrategyFactory {

    private static final Map<String, DiscountStrategy> strategyCache = new ConcurrentHashMap<>();

    public static synchronized DiscountStrategy getDiscountStrategy(Discount discount) {
        String key = discount.getId();
        DiscountStrategy cachedStrategy = strategyCache.get(key);
        if (cachedStrategy != null) {
            log.debug("Discount strategy already exists for discount id {}", discount);
            cachedStrategy.update(discount);
            log.debug("Cached discount strategy updated {} ", cachedStrategy);
            return cachedStrategy;
        }
        DiscountStrategy newStrategy;
        switch (discount.getId()) {
            case "PERCENTAGE":
                newStrategy = new PercentageDiscount(discount);
                strategyCache.put(key, newStrategy);
                return newStrategy;
            case "QUANTITY":
                newStrategy = new QuantityDiscount(discount);
                strategyCache.put(key, newStrategy);
                return newStrategy;
            case "BUYXGETYDISCOUNT":
                newStrategy = new BuyXGetYDiscount(discount);
                strategyCache.put(key, newStrategy);
                return newStrategy;
            default:
                throw new RuntimeException("No such discount strategy: " + discount.getId());
        }
    }

}
