package com.abdelkhalek.storehub.order.infrastructure.models.slot;

import com.abdelkhalek.storehub.order.domain.models.DeliveryMode;
import lombok.Data;

@Data
public class DeliveryRequest {
    DeliveryMode mode;
    AddressRequest address;
}
