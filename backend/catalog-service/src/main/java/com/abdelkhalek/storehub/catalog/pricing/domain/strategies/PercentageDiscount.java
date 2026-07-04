package com.abdelkhalek.storehub.catalog.pricing.domain.strategies;

import com.abdelkhalek.storehub.catalog.pricing.DiscountWithProductIds;
import com.abdelkhalek.storehub.catalog.pricing.PercentageOff;
import com.abdelkhalek.storehub.catalog.pricing.domain.models.AppliedDiscount;
import com.abdelkhalek.storehub.catalog.pricing.domain.models.Cart;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.List;
import java.util.UUID;

@Slf4j
@Data
public class PercentageDiscount implements DiscountStrategy {
    private BigDecimal percentage;
    private List<UUID> productIds; // null means applies to all products
    private String id;


    // Construct an instance(which is a discount strategy) using a standard Discount object
    public PercentageDiscount(DiscountWithProductIds discount) {
        this.id = discount.getType().name().toLowerCase();
        PercentageOff percentageOff = (PercentageOff) discount.getRule();
        this.percentage = percentageOff.percent();
        this.productIds = discount.getProductIds();
    }

    // Update field values using a standard Discount object (standard because all strategies uses a Discount object)
    public void update(DiscountWithProductIds discount) {
        log.debug("Updating new discount {} ", discount);
        log.debug("get discount rules {}", discount.getRule());
        PercentageOff percentageOff = (PercentageOff) discount.getRule();
        percentage = percentageOff.percent();
        log.debug("productIds {} ", discount.getProductIds());
    }

    @Override
    public void apply(Cart cart) {
        log.debug("Now we will apply percentage discount for {}: with {}%\n", productIds,
                percentage);
        cart.getItems().forEach(item -> {
            log.debug("now we will check the item with productId {}", item.getProductId() + "\n");
            // Check if discount applies to this item
            boolean applies = (productIds == null || productIds.contains(item.getProductId()));

            if (applies) {
                log.debug("We apply");
                BigDecimal discountAmount = (item.getOriginalUnitPrice().multiply(percentage))
                        .divide(BigDecimal.valueOf(100), new MathContext(10, RoundingMode.HALF_UP));
                // or .divide(BigDecimal.valueOf(100),10, RoundingMode.HALF_UP);

                String description = percentage + "% discount";

                AppliedDiscount discount = new AppliedDiscount(description, discountAmount);
                log.debug("the discount amount is per unit {}", discount.getAmountPerUnit());
                log.debug("item before applying discount: {}", item);
                item.applyDiscount(discount);
                log.debug("item: {}", item);
            } else {
                log.debug("Not apply");
            }
        });

        cart.calculateTotalDiscount();
    }
}