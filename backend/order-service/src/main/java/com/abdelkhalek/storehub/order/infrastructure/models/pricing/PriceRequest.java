package com.abdelkhalek.storehub.order.infrastructure.models.pricing;

import com.abdelkhalek.storehub.order.infrastructure.models.CartItemRequest;
import lombok.Data;

import java.util.List;

@Data
public class PriceRequest {

    List<CartItemRequest> items;
}
