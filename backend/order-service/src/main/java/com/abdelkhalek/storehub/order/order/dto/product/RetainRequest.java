package com.abdelkhalek.storehub.order.order.dto.product;

import java.util.List;
import java.util.UUID;

public record RetainRequest(List<CartItemRequest> items,
                            UUID storeId) {


}
