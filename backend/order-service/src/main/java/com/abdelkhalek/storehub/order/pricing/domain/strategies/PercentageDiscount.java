package com.abdelkhalek.storehub.order.pricing.domain.strategies;

import com.abdelkhalek.storehub.order.pricing.domain.models.AppliedDiscount;
import com.abdelkhalek.storehub.order.pricing.domain.models.Cart;
import com.abdelkhalek.storehub.order.pricing.domain.models.Discount;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

@Slf4j
@Data
public class PercentageDiscount implements DiscountStrategy {
    private BigDecimal percentage;
    private String productId; // null means applies to all products
    private String id;

    public PercentageDiscount(String id, BigDecimal percentage, String productId) {
        this.percentage = percentage;
        this.productId = productId;
        this.id = id;
    }

    // Construct an instance(which is a discount strategy) using a standard Discount object
    public PercentageDiscount(Discount discount){
        this(
                discount.getId(),
                BigDecimal.valueOf(Integer.parseInt(discount.getAttributes().get("percentage"))),
                discount.getProductId()
        );
    }

    // Update field values using a standard Discount object (standard because all strategies uses a Discount object)
    public void update(Discount discount){
        log.info("Updating new discount {} ", discount);
        log.info("get attributes {}", discount.getAttributes());
        percentage = BigDecimal.valueOf(Integer.parseInt(discount.getAttributes().get("percentage")));
        log.info("productId new value1 {} ", discount.getAttributes().get("productId"));
        productId = discount.getProductId();
        log.info("productId new value2 {} ", discount.getAttributes().get("productId"));
    }

    @Override
    public void apply(Cart cart) {
        log.info("Now we will apply percentage discount for {}: {}%\n", productId , percentage);
        cart.getItems().forEach(item -> {
            log.info("now we will check the item with productId {}" , item.getProductId()+"\n");
            // Check if discount applies to this item
            boolean applies = (productId == null || productId.equals(item.getProductId()));

            if (applies) {
                log.info("We apply");
                BigDecimal discountAmount = (
                        item.getOriginalUnitPrice()
                                .multiply(percentage)).divide(BigDecimal.valueOf(100),new MathContext(10, RoundingMode.HALF_UP)
                );// or .divide(BigDecimal.valueOf(100),10, RoundingMode.HALF_UP);

                String description = percentage + "% discount";

                AppliedDiscount discount = new AppliedDiscount(description,discountAmount);
                log.info("the discount amount is per unit {}", discount.getAmountPerUnit());
                log.info("item before applying discount: {}",item);
                item.applyDiscount(discount);
                log.info("item: {}",item);
            } else {
                log.info("Not apply");
            }
        });

        cart.calculateTotalDiscount();
    }
}