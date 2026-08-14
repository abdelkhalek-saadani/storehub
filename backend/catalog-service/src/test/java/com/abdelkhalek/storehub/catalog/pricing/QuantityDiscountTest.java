package com.abdelkhalek.storehub.catalog.pricing;

import com.abdelkhalek.storehub.catalog.pricing.domain.models.Cart;
import com.abdelkhalek.storehub.catalog.pricing.domain.models.Item;
import com.abdelkhalek.storehub.catalog.pricing.domain.strategies.QuantityDiscount;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

class QuantityDiscountTest {

    @Test
    void apply_appliesDiscount_whenQuantityMeetsMinimum() {
        QuantityDiscount strategy = new QuantityDiscount("id", 5, BigDecimal.TEN, null);
        Item item = itemWithPrice(UUID.randomUUID(), BigDecimal.valueOf(100), 5);
        Cart cart = cartOf(item);

        strategy.apply(cart);

        assertThat(item.getAppliedDiscounts()).isNotEmpty();
    }

    @Test
    void apply_skipsDiscount_whenQuantityBelowMinimum() {
        QuantityDiscount strategy = new QuantityDiscount("id", 5, BigDecimal.TEN, null);
        Item item = itemWithPrice(UUID.randomUUID(), BigDecimal.valueOf(100), 4);
        Cart cart = cartOf(item);

        strategy.apply(cart);

        assertThat(item.getAppliedDiscounts()).isEmpty();
    }

    @Test
    void apply_appliesDiscount_whenProductIdsNull() {
        QuantityDiscount strategy = new QuantityDiscount("id", 1, BigDecimal.TEN, null);
        Item item = itemWithPrice(UUID.randomUUID(), BigDecimal.valueOf(100), 1);
        Cart cart = cartOf(item);

        strategy.apply(cart);

        assertThat(item.getAppliedDiscounts()).isNotEmpty();
    }

    @Test
    void apply_skipsItem_whenProductNotInList() {
        QuantityDiscount strategy = new QuantityDiscount("id", 1, BigDecimal.TEN, List.of(UUID.randomUUID()));
        Item item = itemWithPrice(UUID.randomUUID(), BigDecimal.valueOf(100), 5);
        Cart cart = cartOf(item);

        strategy.apply(cart);

        assertThat(item.getAppliedDiscounts()).isEmpty();
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
