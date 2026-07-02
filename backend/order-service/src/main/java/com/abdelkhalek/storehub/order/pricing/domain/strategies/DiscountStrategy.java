package com.abdelkhalek.storehub.order.pricing.domain.strategies;


import com.abdelkhalek.storehub.order.pricing.domain.models.Cart;
import com.abdelkhalek.storehub.order.pricing.domain.models.Discount;

public interface DiscountStrategy {
//    String getId();
    void apply(Cart cart);
    void update(Discount discount);
}