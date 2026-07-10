package com.abdelkhalek.storehub.order.cart.dtos;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.UUID;

public record UpdateCartRequest(
        @NotNull UUID storeId,
        @NotEmpty List<@Valid CartItem> items
) {}