package com.abdelkhalek.storehub.order.cart.dtos;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record AddItemRequest(
        @NotNull UUID productId,
        @NotNull UUID storeId,
        @Min(0) int quantity
) {}