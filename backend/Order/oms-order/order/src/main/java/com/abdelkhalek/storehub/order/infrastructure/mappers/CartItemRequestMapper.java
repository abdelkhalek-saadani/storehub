package com.abdelkhalek.storehub.order.infrastructure.mappers;

import com.abdelkhalek.storehub.order.domain.models.CartItem;
import com.abdelkhalek.storehub.order.infrastructure.models.CartItemRequest;
import com.abdelkhalek.storehub.order.infrastructure.models.pricing.CartItemResponse;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CartItemRequestMapper {

    CartItem toCartItem(CartItemRequest cartItemRequest);

    CartItemRequest fromCartItem(CartItem cartItem);

    List<CartItemRequest> fromCartItems(List<CartItem> cartItems);


    CartItem toCartItem(CartItemResponse cartItem);

    List<CartItem> toCartItems(List<CartItemResponse> cartItems);


}
