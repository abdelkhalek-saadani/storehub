package com.abdelkhalek.storehub.catalog.pricing.domain.strategies;

import com.abdelkhalek.storehub.catalog.pricing.domain.models.DiscountWithProductIds;
import com.abdelkhalek.storehub.catalog.pricing.domain.models.discountrule.Quantity;
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
public class QuantityDiscount implements DiscountStrategy {
    private int minimumQuantity;
    private BigDecimal percentage;
    private List<UUID> productIds;
    private String id;

    public QuantityDiscount( String id,int minimumQuantity,
                             BigDecimal percentage, List<UUID> productIds) {

        this.minimumQuantity = minimumQuantity;
        this.percentage = percentage;
        this.productIds = productIds;
        this.id = id;
    }

    public QuantityDiscount(DiscountWithProductIds discount) {
        this.id = discount.getType().name().toLowerCase();
        Quantity quantityDiscount = (Quantity) discount.getRule();
        this.minimumQuantity = quantityDiscount.minimumQuantity();
        this.percentage = quantityDiscount.percentage();
        this.productIds = discount.getProductIds();
    }

    public void update(DiscountWithProductIds discount) {
        id = discount.getType().name().toLowerCase();
        Quantity quantityDiscount = (Quantity) discount.getRule();
        percentage = quantityDiscount.percentage();
        minimumQuantity = quantityDiscount.minimumQuantity();
        productIds = discount.getProductIds();
    }

    @Override
    public void apply(Cart cart) {

        log.info("Now we will apply quantity discount for {} ", productIds);
        cart.getItems().forEach(item -> {
            boolean applies = (productIds == null || productIds.contains(item.getProductId())) &&
                    item.getQuantity() >= minimumQuantity;

            if (applies) {
                BigDecimal discountAmount =
                        (item.getOriginalUnitPrice().multiply(percentage))
                                .divide(
                                        BigDecimal.valueOf(100),new MathContext(10, RoundingMode.HALF_UP)
                                );

                String description = percentage + "% discount for buying " +
                        minimumQuantity + " or more";

                AppliedDiscount discount = new AppliedDiscount(
                        description, discountAmount
                );

                item.applyDiscount(discount);
            }
        });

        cart.calculateTotalDiscount();
    }
}
