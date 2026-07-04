package com.abdelkhalek.storehub.catalog.pricing.domain.models;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
public class Cart {
    private UUID storeId;
    private String cartId;
    private List<Item> items = new ArrayList<>();
    private BigDecimal totalDiscount = BigDecimal.ZERO;








    public String toString() {
        calculateTotalDiscount();
        return "The total discount " + totalDiscount + ",\nitems = " + items + "and the total discount = " + totalDiscount + "\n" +
                "   The original total is " + getOriginalTotal() + " and total after discount is " + getFinalTotal();
    }

    public void addItem(Item item) {
        this.items.add(item);
    }

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
        Cart copy = new Cart();
        copy.setCartId(this.cartId);
        copy.setTotalDiscount(this.totalDiscount); // BigDecimal is immutable, safe to share reference
        copy.setItems(
                this.items.stream()
                        .map(Item::copy)
                        .collect(Collectors.toCollection(ArrayList::new)) // keep it mutable like the original
        );
        return copy;
    }
}
