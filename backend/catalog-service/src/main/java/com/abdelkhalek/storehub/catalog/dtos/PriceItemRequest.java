package com.abdelkhalek.storehub.catalog.dtos;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class PriceItemRequest {

    @NotNull
    UUID productId;

    @NotNull
    int quantity;
}
