package com.abdelkhalek.storehub.order.infrastructure.mappers;

import com.abdelkhalek.storehub.order.order.entity.OrderEntity;
import com.abdelkhalek.storehub.order.order.models.Order;
import org.mapstruct.Mapper;

@Mapper(
        componentModel = "spring"
)
public interface OrderEntityMapper {

    OrderEntity fromOrder(Order order);
    Order toOrder(OrderEntity orderEntity);



}
