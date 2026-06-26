package com.abdelkhalek.storehub.order.infrastructure.models.pricing;

import com.abdelkhalek.storehub.order.domain.models.Money;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
public class CartItemResponse {

    private String productId;
    private int quantity;
    private Money subtotal;
    private Money unitPrice;
    private Money originalUnitPrice;

}
