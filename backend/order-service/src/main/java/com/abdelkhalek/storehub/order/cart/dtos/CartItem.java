package com.abdelkhalek.storehub.order.cart.dtos;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record CartItem(
        @NotNull UUID productId,
        @Min(0) int quantity
) {}