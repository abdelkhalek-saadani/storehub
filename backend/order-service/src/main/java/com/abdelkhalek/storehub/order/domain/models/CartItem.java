package com.abdelkhalek.storehub.order.domain.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
public class CartItem {
    private UUID id;
    private UUID productId;
    private int quantity;
    private BigDecimal subtotal;
    private BigDecimal unitPrice;
    private BigDecimal originalUnitPrice;

    LocalDateTime createdAt;
    LocalDateTime updatedAt;


    public CartItem(UUID productId, int quantity){
        this.productId = productId;
        this.quantity = quantity;
    }
    public CartItem(UUID itemId, UUID productId, int quantity){
        this.id = itemId;
        this.productId = productId;
        this.quantity = quantity;
    }


}