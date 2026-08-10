package com.abdelkhalek.storehub.order.cart.domain;

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
    UUID guestId;
    UUID storeId;
    BigDecimal originalTotal;
    BigDecimal finalTotal;
    BigDecimal totalDiscount;
    List<CartItem> items;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;



    public Cart upsert(List<CartItem> items) {
        List<UUID> productIds = items.stream().map(CartItem::getProductId).toList();
        List<CartItem> keptItems = this.items.stream()
                .filter(it -> !productIds.contains(it.getProductId()))
                .collect(Collectors.toCollection(ArrayList::new));

        items.forEach(item -> {
            if (item.getQuantity() > 0) {
                keptItems.add(item);
            }
        });

        return new Cart(id, userId, guestId, storeId, originalTotal, finalTotal, totalDiscount,
                keptItems, createdAt,
                LocalDateTime.now());

    }

    public Cart upsert(CartItem item) {
        List<CartItem> updatedItems = this.items.stream()
                .filter(it -> !it.getProductId().equals(item.getProductId()))
                .collect(Collectors.toCollection(ArrayList::new));

        if (item.getQuantity() > 0) {
            updatedItems.add(item);
        }

        return new Cart(id, userId, guestId, storeId, originalTotal, finalTotal, totalDiscount,
                updatedItems, createdAt,
                LocalDateTime.now());

    }


    public Cart merge(List<CartItem> guestItems) {
        List<CartItem> mergedItems = new ArrayList<>(this.items);

        guestItems.forEach(guestItem -> {
            mergedItems.stream()
                    .filter(it -> it.getProductId().equals(guestItem.getProductId()))
                    .findFirst()
                    .ifPresentOrElse(
                            existing -> existing.setQuantity(existing.getQuantity() + guestItem.getQuantity()),
                            () -> mergedItems.add(guestItem)
                    );
        });

        return new Cart(id, userId, guestId, storeId, originalTotal, finalTotal, totalDiscount,
                mergedItems, createdAt, LocalDateTime.now());
    }
}
