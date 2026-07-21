package com.abdelkhalek.storehub.order.order.dto;

import java.util.List;
import java.util.UUID;

public record RetainRequest(List<CartItemRequest> items,
                            UUID storeId) {


}
