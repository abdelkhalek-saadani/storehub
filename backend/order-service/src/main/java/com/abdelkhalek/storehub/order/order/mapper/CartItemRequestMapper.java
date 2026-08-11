package com.abdelkhalek.storehub.order.order.mapper;

import com.abdelkhalek.storehub.order.cart.entity.CartItemEntity;
import com.abdelkhalek.storehub.order.order.dto.product.CartItemRequest;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CartItemRequestMapper {


    CartItemRequest fromCartItemEntity(CartItemEntity cartItemEntity);
    List<CartItemRequest> fromCartItemEntities(List<CartItemEntity> cartItemEntities);
}
