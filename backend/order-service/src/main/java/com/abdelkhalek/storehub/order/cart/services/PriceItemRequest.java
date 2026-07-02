package com.abdelkhalek.storehub.order.cart.services;

import lombok.Data;

import java.util.UUID;

@Data
public class PriceItemRequest {
    UUID productId;
    int quantity;
}
