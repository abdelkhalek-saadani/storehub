package com.abdelkhalek.storehub.order.cart.dtos;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;
import java.util.UUID;

public record GuestCartRequest(
        UUID storeId,
        @NotEmpty List<@Valid GuestCartItem> items
) {}