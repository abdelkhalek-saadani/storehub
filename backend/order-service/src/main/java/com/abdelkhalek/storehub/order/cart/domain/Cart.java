package com.abdelkhalek.storehub.order.cart.domain;

import com.abdelkhalek.storehub.order.cart.exception.CartItemNotFoundException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Cart {

    UUID id;
    UUID userId;
    UUID storeId;
    BigDecimal originalTotal;
    BigDecimal finalTotal;
    BigDecimal totalDiscount;
    List<CartItem> items;
    LocalDateTime createdAt = LocalDateTime.now();
    LocalDateTime updatedAt;


    public Cart upsert(CartItem item) {
        List<CartItem> updatedItems = this.items.stream()
                .filter(it -> !it.getProductId().equals(item.getProductId()))
                .collect(Collectors.toCollection(ArrayList::new));

        if (item.getQuantity() > 0) {
            updatedItems.add(item);
        }

        return new Cart(id, userId, storeId, originalTotal, finalTotal, totalDiscount,
                updatedItems, createdAt,
                LocalDateTime.now());

    }

    public Cart updateItemQuantity(UUID itemId, int quantity) {
        CartItem existing = this.items.stream()
                .filter(it -> it.getId().equals(itemId))
                .findFirst()
                .orElseThrow(() -> new CartItemNotFoundException(itemId));

        List<CartItem> updatedItems = this.items.stream()
                .filter(it -> !it.getId().equals(itemId))
                .collect(Collectors.toCollection(ArrayList::new));

        if (quantity > 0) {
            CartItem updated = new CartItem();
            updated.setId(existing.getId());
            updated.setProductId(existing.getProductId());
            updated.setQuantity(quantity);
            updated.setUnitPrice(existing.getUnitPrice());
            updated.setUnitPrice(existing.getUnitPrice());
            updated.setCreatedAt(existing.getCreatedAt());
            updatedItems.add(updated);
        }

        return new Cart(id, userId, storeId, originalTotal, finalTotal, totalDiscount,
                updatedItems, createdAt,
                LocalDateTime.now());
    }

}
