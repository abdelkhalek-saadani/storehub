package com.abdelkhalek.storehub.catalog.pricing.domain.models;

import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


@Slf4j
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class Item {
    private UUID id;
    private UUID productId;
    private String productName;
    private String productImageUrl;
    private int quantity;
    private BigDecimal originalUnitPrice = BigDecimal.ZERO;
    private BigDecimal finalUnitPrice = BigDecimal.ZERO;
    private List<AppliedDiscount> appliedDiscounts = new ArrayList<>();


    public Item(UUID productId, int quantity, BigDecimal originalUnitPrice) {
        this.productId = productId;
        this.quantity = quantity;
        this.originalUnitPrice = originalUnitPrice;
        this.finalUnitPrice = originalUnitPrice; // Initially the same as original
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.finalUnitPrice = unitPrice;
        this.originalUnitPrice = unitPrice;
    }

    public BigDecimal getSubtotal() {
        return finalUnitPrice.multiply(BigDecimal.valueOf(quantity));
    }

    public BigDecimal getOriginalSubtotal() {
        return originalUnitPrice.multiply(BigDecimal.valueOf(quantity));
    }

    public void applyDiscount(AppliedDiscount discount) {
        this.appliedDiscounts.add(discount);
        log.debug("finalUnitPrice: {}", finalUnitPrice);
        log.debug("discount.getAmountPerUnit: {}", discount.getAmountPerUnit());
        this.finalUnitPrice = finalUnitPrice.subtract(discount.getAmountPerUnit());
    }

    public Item copy() {
        return this.toBuilder().appliedDiscounts(new ArrayList<>(this.appliedDiscounts)).build();
    }


}