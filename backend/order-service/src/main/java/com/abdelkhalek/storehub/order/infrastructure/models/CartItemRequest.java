package com.abdelkhalek.storehub.order.infrastructure.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
public class CartItemRequest {
    private String productId;
    private int quantity;
}