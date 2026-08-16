package com.abdelkhalek.storehub.order.cart.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.UUID;

public record UpdateCartRequest(
        @NotNull UUID storeId,
        @NotEmpty List<@Valid CartItem> items
) {
    public record CartItem(
            @NotNull UUID productId,
            @Min(0) int quantity
    ) {}
}