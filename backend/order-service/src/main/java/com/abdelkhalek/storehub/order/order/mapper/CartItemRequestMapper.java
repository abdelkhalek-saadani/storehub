package com.abdelkhalek.storehub.order.order.mapper;

import com.abdelkhalek.storehub.order.cart.entities.CartItemEntity;
import com.abdelkhalek.storehub.order.order.dto.CartItemRequest;
import com.abdelkhalek.storehub.order.order.models.OrderItem;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CartItemRequestMapper {

    OrderItem toCartItem(CartItemRequest cartItemRequest);

    CartItemRequest fromCartItem(OrderItem cartItem);


    CartItemRequest fromCartItemEntity(CartItemEntity cartItemEntity);
    List<CartItemRequest> fromCartItemEntities(List<CartItemEntity> cartItemEntities);
}
