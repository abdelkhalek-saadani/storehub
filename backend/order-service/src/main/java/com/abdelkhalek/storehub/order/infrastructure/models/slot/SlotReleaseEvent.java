package com.abdelkhalek.storehub.order.infrastructure.models.slot;

import com.abdelkhalek.storehub.order.order.models.DomainEvent;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SlotReleaseEvent implements DomainEvent {
    String retainId;
}
