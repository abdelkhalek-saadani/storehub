package com.abdelkhalek.storehub.order.cart.dtos;


import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record GetCartRequest(
        @NotNull UUID storeId) {
}

