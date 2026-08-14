package com.abdelkhalek.storehub.catalog.pricing;

import com.abdelkhalek.storehub.catalog.pricing.domain.models.Cart;
import com.abdelkhalek.storehub.catalog.pricing.domain.models.DiscountType;
import com.abdelkhalek.storehub.catalog.pricing.domain.models.DiscountWithProductIds;
import com.abdelkhalek.storehub.catalog.pricing.domain.models.Item;
import com.abdelkhalek.storehub.catalog.pricing.domain.models.discountrule.PercentageOff;
import com.abdelkhalek.storehub.catalog.pricing.domain.strategies.PercentageDiscount;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

class PercentageDiscountTest {

    @Test
    void apply_appliesDiscount_whenProductIdsNull() {
        DiscountWithProductIds discount = discount(BigDecimal.TEN, null);
        PercentageDiscount strategy = new PercentageDiscount(discount);
        Item item = itemWithPrice(UUID.randomUUID(), BigDecimal.valueOf(100), 1);
        Cart cart = cartOf(item);

        strategy.apply(cart);

        assertThat(item.getAppliedDiscounts()).isNotEmpty();
    }

    @Test
    void apply_appliesDiscount_onlyToMatchingProductIds() {
        UUID matchingId = UUID.randomUUID();
        DiscountWithProductIds discount = discount(BigDecimal.TEN, List.of(matchingId));
        PercentageDiscount strategy = new PercentageDiscount(discount);
        Item matchingItem = itemWithPrice(matchingId, BigDecimal.valueOf(100), 1);
        Cart cart = cartOf(matchingItem);

        strategy.apply(cart);

        assertThat(matchingItem.getAppliedDiscounts()).isNotEmpty();
    }

    @Test
    void apply_skipsItem_whenProductNotInList() {
        DiscountWithProductIds discount = discount(BigDecimal.TEN, List.of(UUID.randomUUID()));
        PercentageDiscount strategy = new PercentageDiscount(discount);
        Item otherItem = itemWithPrice(UUID.randomUUID(), BigDecimal.valueOf(100), 1);
        Cart cart = cartOf(otherItem);

        strategy.apply(cart);

        assertThat(otherItem.getAppliedDiscounts()).isEmpty();
    }

    @Test
    void apply_computesCorrectDiscountAmount_forGivenPercentage() {
        DiscountWithProductIds discount = discount(BigDecimal.valueOf(20), null);
        PercentageDiscount strategy = new PercentageDiscount(discount);
        Item item = itemWithPrice(UUID.randomUUID(), BigDecimal.valueOf(50), 1);
        Cart cart = cartOf(item);

        strategy.apply(cart);

        // 20% of 50 = 10
        assertThat(item.getAppliedDiscounts().get(0).getAmountPerUnit())
                .isEqualByComparingTo(BigDecimal.TEN);
    }

    private DiscountWithProductIds discount(BigDecimal percent, List<UUID> productIds) {
        return new DiscountWithProductIds(
                UUID.randomUUID(), UUID.randomUUID(), DiscountType.PERCENTAGE_OFF,
                new PercentageOff(percent), Instant.now(), Instant.now().plusSeconds(3600), productIds);
    }

    private Item itemWithPrice(UUID productId, BigDecimal unitPrice, int qty) {
        Item item = new Item();
        item.setProductId(productId);
        item.setUnitPrice(unitPrice);
        item.setQuantity(qty);
        return item;
    }

    private Cart cartOf(Item... items) {
        Cart cart = new Cart();
        cart.setItems(List.of(items));
        return cart;
    }
}
