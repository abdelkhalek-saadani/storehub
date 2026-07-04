package com.abdelkhalek.storehub.catalog.dtos;

import lombok.Data;

import java.util.UUID;

@Data
public class PriceItemRequest {
    UUID productId;
    int quantity;
}
