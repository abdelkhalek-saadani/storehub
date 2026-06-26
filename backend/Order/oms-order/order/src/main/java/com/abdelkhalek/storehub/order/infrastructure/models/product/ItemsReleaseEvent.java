package com.abdelkhalek.storehub.order.infrastructure.models.product;

import com.abdelkhalek.storehub.order.domain.models.DomainEvent;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ItemsReleaseEvent implements DomainEvent {
    String retainId;
}
