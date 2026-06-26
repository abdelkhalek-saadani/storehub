package com.abdelkhalek.storehub.order.infrastructure.mappers;

import com.abdelkhalek.storehub.order.domain.models.Delivery;
import com.abdelkhalek.storehub.order.infrastructure.models.slot.DeliveryRequest;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface DeliveryRequestMapper {

    DeliveryRequest fromDelivery(Delivery delivery);

}
