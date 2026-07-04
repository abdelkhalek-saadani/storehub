package com.abdelkhalek.storehub.catalog.dtos;

import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class PricesRequest {
    UUID storeId;
    List<PriceItemRequest> items;
}
