package com.abdelkhalek.storehub.order.cart;

import com.abdelkhalek.storehub.order.cart.domain.Cart;
import com.abdelkhalek.storehub.order.cart.domain.CartItem;
import com.abdelkhalek.storehub.order.cart.dtos.CartItemResponse;
import com.abdelkhalek.storehub.order.cart.dtos.UpdateCartRequest;
import com.abdelkhalek.storehub.order.cart.dtos.CartResponse;
import com.abdelkhalek.storehub.order.cart.entities.CartEntity;
import com.abdelkhalek.storehub.order.cart.entities.CartItemEntity;
import com.abdelkhalek.storehub.order.shared.model.AppliedOffer;
import com.abdelkhalek.storehub.order.shared.dto.PriceItemResponse;
import com.abdelkhalek.storehub.order.shared.dto.PricesRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CartMapper {

    PricesRequest fromGuestCartRequestToPricesRequest(UpdateCartRequest guestCartRequest);

    List<CartItem> fromPriceItemsResponse(List<PriceItemResponse> priceItemsResponse);


    PricesRequest toPricesRequest(Cart cart);

    @Mapping(target = "appliedOfferLabel", source = "appliedOffer", qualifiedByName = "offerLabel")
    CartItemEntity toCartItemEntity(CartItem cartItem);

    @Named("offerLabel")
    default String mapOfferLabel(AppliedOffer offer) {
        return offer != null ? offer.getLabel() : null;
    }

    CartEntity toEntity(Cart cart);


    @Mapping(target = "cartId", source = "id")
    CartResponse fromEntityToResponse(CartEntity entity);

    @Mapping(target = "itemId", source = "id")
    CartItemResponse fromItemEntityToItemResponse(CartItemEntity entity);

    Cart fromEntityToDomain(CartEntity entity);


}
