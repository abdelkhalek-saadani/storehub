package com.abdelkhalek.storehub.catalog.pricing;

import com.abdelkhalek.storehub.catalog.pricing.domain.models.Cart;
import com.abdelkhalek.storehub.catalog.pricing.domain.models.DiscountType;
import com.abdelkhalek.storehub.catalog.pricing.domain.models.DiscountWithProductIds;
import com.abdelkhalek.storehub.catalog.pricing.domain.models.Item;
import com.abdelkhalek.storehub.catalog.pricing.domain.models.discountrule.BuyXGetY;
import com.abdelkhalek.storehub.catalog.pricing.domain.strategies.BuyXGetYDiscount;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

class BuyXGetYDiscountTest {

    @Test
    void apply_appliesFreeItemsDiscount_whenQuantityAllowsFullSets() {
        // required=2, free=1 -> set size 3; qty=6 -> 2 full sets -> 2 free items
        DiscountWithProductIds discount = discount(2, 1, null);
        BuyXGetYDiscount strategy = new BuyXGetYDiscount(discount);
        Item item = itemWithPrice(UUID.randomUUID(), BigDecimal.valueOf(30), 6);
        Cart cart = cartOf(item);

        strategy.apply(cart);

        assertThat(item.getAppliedDiscounts()).isNotEmpty();
    }

    @Test
    void apply_skipsDiscount_whenQuantityBelowRequired() {
        DiscountWithProductIds discount = discount(5, 1, null);
        BuyXGetYDiscount strategy = new BuyXGetYDiscount(discount);
        Item item = itemWithPrice(UUID.randomUUID(), BigDecimal.valueOf(30), 3);
        Cart cart = cartOf(item);

        strategy.apply(cart);

        assertThat(item.getAppliedDiscounts()).isEmpty();
    }

    @Test
    void apply_appliesNoDiscount_whenQuantityBelowOneFullSet() {
        // required=2, free=1 -> set size 3; qty=2 meets "required" but not a full set
        DiscountWithProductIds discount = discount(2, 1, null);
        BuyXGetYDiscount strategy = new BuyXGetYDiscount(discount);
        Item item = itemWithPrice(UUID.randomUUID(), BigDecimal.valueOf(30), 2);
        Cart cart = cartOf(item);

        strategy.apply(cart);

        assertThat(item.getAppliedDiscounts()).isEmpty();
    }

    @Test
    void apply_appliesDiscount_whenProductIdsNull() {
        DiscountWithProductIds discount = discount(2, 1, null);
        BuyXGetYDiscount strategy = new BuyXGetYDiscount(discount);
        Item item = itemWithPrice(UUID.randomUUID(), BigDecimal.valueOf(30), 3);
        Cart cart = cartOf(item);

        strategy.apply(cart);

        assertThat(item.getAppliedDiscounts()).isNotEmpty();
    }

    private DiscountWithProductIds discount(int requiredQty, int freeQty, List<UUID> productIds) {
        return new DiscountWithProductIds(
                UUID.randomUUID(), UUID.randomUUID(), DiscountType.BUY_X_GET_Y,
                new BuyXGetY(requiredQty, freeQty), Instant.now(), Instant.now().plusSeconds(3600), productIds);
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
