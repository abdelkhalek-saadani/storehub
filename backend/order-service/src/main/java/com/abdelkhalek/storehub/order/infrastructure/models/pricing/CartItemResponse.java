package com.abdelkhalek.storehub.order.infrastructure.models.pricing;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
public class CartItemResponse {

    private String productId;
    private int quantity;
    private BigDecimal subtotal;
    private BigDecimal unitPrice;
    private BigDecimal originalUnitPrice;

}
