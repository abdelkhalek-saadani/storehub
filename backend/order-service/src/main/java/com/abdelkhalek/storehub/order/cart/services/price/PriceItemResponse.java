package com.abdelkhalek.storehub.order.cart.services.price;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PriceItemResponse {
    UUID productId;
    int quantity;
    BigDecimal unitPrice;
    BigDecimal originalLineTotal;
    BigDecimal discountAmount;
    BigDecimal finalLineTotal;
    AppliedOffer appliedOffer;
}
