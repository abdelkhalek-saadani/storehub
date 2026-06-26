package com.abdelkhalek.storehub.order.infrastructure.models.slot;

import com.abdelkhalek.storehub.order.infrastructure.models.StoreRequest;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AvailabilityRequest {

    DeliveryRequest delivery;
    SlotRequest slot;
    StoreRequest store;

}
