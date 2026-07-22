package com.abdelkhalek.storehub.order.cart.domain;

import com.abdelkhalek.storehub.order.cart.entities.CartEntity;
import com.abdelkhalek.storehub.order.shared.model.AppliedOffer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartItem {

    UUID id;
    String productName;
    UUID productId;
    int quantity;

    BigDecimal unitPrice;
    BigDecimal originalLineTotal;
    BigDecimal discountAmount;
    BigDecimal finalLineTotal;
    AppliedOffer appliedOffer;

    CartEntity cart;

    LocalDateTime createdAt;




    public CartItem(UUID productId, int quantity) {
        this.productId = productId;
        this.quantity = quantity;
    }


}
