package com.abdelkhalek.storehub.order.order.models;

import com.abdelkhalek.storehub.order.cart.services.price.AppliedOffer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
public class OrderItem {

    UUID id;
    String productName;
    UUID productId;
    int quantity;

    BigDecimal unitPrice;
    BigDecimal originalLineTotal;
    BigDecimal discountAmount;
    BigDecimal finalLineTotal;
    AppliedOffer appliedOffer;

    Order order;

    LocalDateTime createdAt;




    public OrderItem(UUID productId, int quantity) {
        this.productId = productId;
        this.quantity = quantity;
    }


}