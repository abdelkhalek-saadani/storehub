package com.abdelkhalek.storehub.catalog.pricing;

import com.abdelkhalek.storehub.catalog.pricing.domain.factories.DiscountStrategyFactory;
import com.abdelkhalek.storehub.catalog.pricing.domain.models.DiscountType;
import com.abdelkhalek.storehub.catalog.pricing.domain.models.DiscountWithProductIds;
import com.abdelkhalek.storehub.catalog.pricing.domain.models.discountrule.BuyXGetY;
import com.abdelkhalek.storehub.catalog.pricing.domain.models.discountrule.DiscountRule;
import com.abdelkhalek.storehub.catalog.pricing.domain.models.discountrule.PercentageOff;
import com.abdelkhalek.storehub.catalog.pricing.domain.models.discountrule.Quantity;
import com.abdelkhalek.storehub.catalog.pricing.domain.strategies.BuyXGetYDiscount;
import com.abdelkhalek.storehub.catalog.pricing.domain.strategies.DiscountStrategy;
import com.abdelkhalek.storehub.catalog.pricing.domain.strategies.PercentageDiscount;
import com.abdelkhalek.storehub.catalog.pricing.domain.strategies.QuantityDiscount;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

class DiscountStrategyFactoryTest {

    @Test
    void getDiscountStrategy_createsPercentageDiscount_forPercentageOffRule() {
        DiscountWithProductIds discount = discountWithRule(
                DiscountType.PERCENTAGE_OFF, new PercentageOff(BigDecimal.TEN));

        DiscountStrategy strategy = DiscountStrategyFactory.getDiscountStrategy(discount);

        assertThat(strategy).isInstanceOf(PercentageDiscount.class);
    }

    @Test
    void getDiscountStrategy_createsQuantityDiscount_forQuantityRule() {
        DiscountWithProductIds discount = discountWithRule(
                DiscountType.QUANTITY, new Quantity(5, BigDecimal.TEN));

        DiscountStrategy strategy = DiscountStrategyFactory.getDiscountStrategy(discount);

        assertThat(strategy).isInstanceOf(QuantityDiscount.class);
    }

    @Test
    void getDiscountStrategy_createsBuyXGetYDiscount_forBuyXGetYRule() {
        DiscountWithProductIds discount = discountWithRule(
                DiscountType.BUY_X_GET_Y, new BuyXGetY(2, 1));

        DiscountStrategy strategy = DiscountStrategyFactory.getDiscountStrategy(discount);

        assertThat(strategy).isInstanceOf(BuyXGetYDiscount.class);
    }

    @Test
    void getDiscountStrategy_throwsRuntimeException_forUnknownRule() {
        DiscountWithProductIds discount = discountWithRule(DiscountType.PERCENTAGE_OFF, null);

        assertThatThrownBy(() -> DiscountStrategyFactory.getDiscountStrategy(discount))
                .isInstanceOf(RuntimeException.class);
    }

    private DiscountWithProductIds discountWithRule(DiscountType type, DiscountRule rule) {
        return new DiscountWithProductIds(
                UUID.randomUUID(), UUID.randomUUID(), type, rule,
                Instant.now(), Instant.now().plusSeconds(3600), List.of());
    }
}
