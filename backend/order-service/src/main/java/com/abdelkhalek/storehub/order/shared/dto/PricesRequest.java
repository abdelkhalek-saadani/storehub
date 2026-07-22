package com.abdelkhalek.storehub.order.shared.dto;

import java.util.List;
import java.util.UUID;

public record PricesRequest (
    UUID storeId,
    List<PriceItemRequest> items
){}
