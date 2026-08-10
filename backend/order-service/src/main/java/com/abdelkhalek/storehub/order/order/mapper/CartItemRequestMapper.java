package com.abdelkhalek.storehub.order.order.mapper;

import com.abdelkhalek.storehub.order.cart.entity.CartItemEntity;
import com.abdelkhalek.storehub.order.order.dto.product.CartItemRequest;
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
