package proxiad.oms.pricing.domain.models;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.proxiad.events.CartItemEvent;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;


@Slf4j
@Data
@NoArgsConstructor
public class Item {
    private String productId;
    private int quantity;
    private BigDecimal originalUnitPrice = BigDecimal.ZERO;
    private BigDecimal finalUnitPrice  = BigDecimal.ZERO;
    private List<AppliedDiscount> appliedDiscounts = new ArrayList<>();


    public Item(String productId, int quantity, BigDecimal originalUnitPrice) {
        this.productId = productId;
        this.quantity = quantity;
        this.originalUnitPrice = originalUnitPrice;
        this.finalUnitPrice = originalUnitPrice; // Initially the same as original
    }

    public BigDecimal getSubtotal() {
        return finalUnitPrice.multiply(BigDecimal.valueOf(quantity));
    }

    public BigDecimal getOriginalSubtotal() {
        return originalUnitPrice.multiply(BigDecimal.valueOf(quantity));
    }

    public void initializePrices(BigDecimal price) {
        this.originalUnitPrice = price;
        this.finalUnitPrice = price;
    }

    // Keep it here, anemic models goes against DDD approach, applyDiscount is a behaviour that belongs to Item
    public void applyDiscount(AppliedDiscount discount) {
        this.appliedDiscounts.add(discount);
        log.info("finalUnitPrice: {}", finalUnitPrice);
        log.info("discount.getAmountPerUnit: {}", discount.getAmountPerUnit());
        this.finalUnitPrice = finalUnitPrice.subtract(discount.getAmountPerUnit());
    }

//    public String toString() {
//        return "productId = " + productId + ", quantity = " + quantity + "the original subtotal is "+ getOriginalSubtotal() + " and after discounts applies it is "+ getSubtotal() +"\n, originalUnitPrice = " + originalUnitPrice + ", finalUnitPrice = " + finalUnitPrice + ", appliedDiscounts = " + appliedDiscounts;
//    }

}