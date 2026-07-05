package com.abdelkhalek.storehub.catalog.pricing.domain.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@Builder(toBuilder = true)
@AllArgsConstructor
public class Cart {
    private UUID storeId;
    private String cartId;
    private List<Item> items = new ArrayList<>();
    private BigDecimal totalDiscount = BigDecimal.ZERO;

    public BigDecimal getOriginalTotal() {
        return items.stream()
                .map(Item::getOriginalSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal getFinalTotal() {
        return items.stream()
                .map(Item::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public void calculateTotalDiscount() {
        this.totalDiscount = getOriginalTotal().subtract(getFinalTotal());
    }

    public Cart copy() {
        return this.toBuilder()
                .items(this.items.stream()
                        .map(Item::copy)
                        .collect(Collectors.toCollection(ArrayList::new)))
                .build();
    }
}
