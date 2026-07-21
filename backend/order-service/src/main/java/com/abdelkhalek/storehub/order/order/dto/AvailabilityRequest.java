package com.abdelkhalek.storehub.order.order.dto;

import java.util.List;
import java.util.UUID;


public record AvailabilityRequest (
    List<CartItemRequest> items,
    UUID storeId
){}
