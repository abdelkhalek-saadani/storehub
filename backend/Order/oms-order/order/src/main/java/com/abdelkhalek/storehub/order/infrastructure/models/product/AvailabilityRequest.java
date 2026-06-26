package com.abdelkhalek.storehub.order.infrastructure.models.product;

import com.abdelkhalek.storehub.order.infrastructure.models.CartItemRequest;
import com.abdelkhalek.storehub.order.infrastructure.models.StoreRequest;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class AvailabilityRequest {
    List<CartItemRequest> items;
    StoreRequest store;
}
