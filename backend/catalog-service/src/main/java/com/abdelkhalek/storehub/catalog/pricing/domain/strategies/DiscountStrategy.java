package com.abdelkhalek.storehub.catalog.pricing.domain.strategies;


import com.abdelkhalek.storehub.catalog.pricing.DiscountWithProductIds;
import com.abdelkhalek.storehub.catalog.pricing.domain.models.Cart;

public interface DiscountStrategy {
    void apply(Cart cart);
    void update(DiscountWithProductIds discount);
}