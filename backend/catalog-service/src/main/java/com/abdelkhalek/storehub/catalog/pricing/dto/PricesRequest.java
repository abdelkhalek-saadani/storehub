package com.abdelkhalek.storehub.catalog.pricing.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class PricesRequest {

    @NotNull
    UUID storeId;

    @NotEmpty
    List<@Valid PriceItemRequest> items;
}
