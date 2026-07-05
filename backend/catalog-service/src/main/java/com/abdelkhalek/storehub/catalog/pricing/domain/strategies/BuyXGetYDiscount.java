package com.abdelkhalek.storehub.catalog.pricing.domain.strategies;


import com.abdelkhalek.storehub.catalog.pricing.domain.models.discountrule.BuyXGetY;
import com.abdelkhalek.storehub.catalog.pricing.domain.models.DiscountWithProductIds;
import com.abdelkhalek.storehub.catalog.pricing.domain.models.AppliedDiscount;
import com.abdelkhalek.storehub.catalog.pricing.domain.models.Cart;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public class BuyXGetYDiscount implements DiscountStrategy {
    private String id;
    private int requiredQuantity;
    private int freeQuantity;
    private List<UUID> productIds;

    /**
     * Construct an instance(which is a discount strategy) using a standard Discount object
     */
    public BuyXGetYDiscount(DiscountWithProductIds discount) {
        id = discount.getType().name().toLowerCase();
        productIds = discount.getProductIds();
        BuyXGetY buyXGetY = (BuyXGetY) discount.getRule();
        requiredQuantity = buyXGetY.requiredQty();
        freeQuantity = buyXGetY.freeQty();
    }

    /**
     * Update field values using a standard Discount object (standard because all strategies uses a
     * Discount object)
     */
    public void update(DiscountWithProductIds discount) {
        productIds = discount.getProductIds();
        BuyXGetY buyXGetY = (BuyXGetY) discount.getRule();
        requiredQuantity = buyXGetY.requiredQty();
        freeQuantity = buyXGetY.freeQty();
    }

    @Override
    public void apply(Cart cart) {
        cart.getItems().forEach(item -> {
            boolean applies = (productIds == null || productIds.contains(item.getProductId())) &&
                    item.getQuantity() >= requiredQuantity;

            if (applies) {
                // Calculate how many free items the customer gets
                int sets = item.getQuantity() / (requiredQuantity + freeQuantity);
                int totalFreeItems = sets * freeQuantity;

                // If there are free items, apply the discount
                if (totalFreeItems > 0) {
                    // The discount per unit is the original price divided by total items
                    // multiplied by the number of free items
                    // (freeItemsCount x unitPrice) : we will substitute this value from the item subtotal
                    // In order to convert it to a value we substitute from each item (which is effectiveDiscountPerUnit),
                    // we divide it by the total qty
                    // (freeItemsCount x unitPrice) / qty:
                    BigDecimal effectiveDiscountPerUnit =
                            item.getOriginalUnitPrice()
                                    .multiply(
                                            BigDecimal.valueOf((double) totalFreeItems / item.getQuantity())
                                    );
                    // For Discount of type buy 2 u get the third off by 50%, same as this, we just add the * percentage / 100
                    // int effectiveDiscountPerUnit = ((item.getOriginalUnitPrice() * percentage)/100) * (totalFreeItems/item.getQuantity());

                    String description = "Buy " + requiredQuantity + " get " +
                            freeQuantity + " free items discount";

                    AppliedDiscount discount = new AppliedDiscount(
                            description, effectiveDiscountPerUnit
                    );

                    item.applyDiscount(discount);
                }
            }
        });

        // This is performed to keep the cart.toString() updated with the total discount value (only for debugging purposes)
        cart.calculateTotalDiscount();
    }
}