package com.abdelkhalek.storehub.order.infrastructure.models.slot;

import com.abdelkhalek.storehub.order.infrastructure.models.StoreRequest;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class RetainRequest {
    final DeliveryRequest delivery;
    final SlotRequest slot;
    final StoreRequest store;
}
