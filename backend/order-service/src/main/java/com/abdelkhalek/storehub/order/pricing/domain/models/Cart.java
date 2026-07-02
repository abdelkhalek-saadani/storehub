package com.abdelkhalek.storehub.order.pricing.domain.models;

import com.abdelkhalek.storehub.order.cart.entities.CartEntity;
import com.abdelkhalek.storehub.order.cart.entities.CartItemEntity;
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

    public CartEntity toEntity() {
        CartEntity entity = new CartEntity();
        entity.setId(UUID.fromString(cartId));
        entity.setFinalTotal(getFinalTotal());
        entity.setOriginalTotal(getFinalTotal());
        entity.setItems(toEntityItems());
        return entity;
    }





    public List<CartItemEntity> toEntityItems() {
        List<CartItemEntity> items = new ArrayList<>();
        for (Item item : this.items) {
            CartItemEntity itemEntity = new CartItemEntity();
            itemEntity.setQuantity(item.getQuantity());
            itemEntity.setProductId(UUID.fromString(item.getProductId()));
            items.add(itemEntity);
        }
        return items;
    }

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
