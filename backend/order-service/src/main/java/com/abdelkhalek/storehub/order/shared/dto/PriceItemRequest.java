package com.abdelkhalek.storehub.order.shared.dto;

import java.util.UUID;

public record PriceItemRequest (
    UUID productId,
    int quantity
){}
