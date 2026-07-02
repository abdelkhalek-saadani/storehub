package com.abdelkhalek.storehub.order.cart.dtos;

import jakarta.validation.constraints.Min;

import java.util.UUID;

public record UpdateItemRequest(
        @Min(0) int quantity,  // 0 is treated as "remove" in updateItem()
        UUID storeId
) {}