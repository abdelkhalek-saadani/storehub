package com.abdelkhalek.storehub.order.infrastructure.mappers;

import com.abdelkhalek.storehub.order.domain.models.Order;
import com.abdelkhalek.storehub.order.infrastructure.models.pricing.PriceRequest;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PriceRequestMapper {

    PriceRequest fromOrder(Order order);

}
