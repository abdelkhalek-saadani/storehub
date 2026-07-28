package com.abdelkhalek.storehub.order.order.dto.product;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
public class CartItemRequest {
    private UUID productId;
    private int quantity;
}