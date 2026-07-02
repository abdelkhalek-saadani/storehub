package com.abdelkhalek.storehub.order.cart;

import com.abdelkhalek.storehub.order.cart.domain.Cart;
import com.abdelkhalek.storehub.order.cart.domain.CartItem;
import com.abdelkhalek.storehub.order.cart.dtos.CartItemResponse;
import com.abdelkhalek.storehub.order.cart.dtos.CartResponse;
import com.abdelkhalek.storehub.order.cart.dtos.GuestCartItem;
import com.abdelkhalek.storehub.order.cart.dtos.GuestCartRequest;
import com.abdelkhalek.storehub.order.cart.entities.CartEntity;
import com.abdelkhalek.storehub.order.cart.entities.CartItemEntity;
import com.abdelkhalek.storehub.order.cart.services.AppliedOffer;
import com.abdelkhalek.storehub.order.cart.services.PriceItemResponse;
import com.abdelkhalek.storehub.order.cart.services.PricesRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CartMapper {

    PricesRequest fromGuestCartRequestToPricesRequest(GuestCartRequest guestCartRequest);

    List<CartItem> fromPriceItemsResponse(List<PriceItemResponse> priceItemsResponse);

    CartItem fromPriceItemResponse(PriceItemResponse priceItemResponse);

    PricesRequest toPricesRequest(Cart cart);

    List<com.abdelkhalek.storehub.order.pricing.domain.models.Item> fromCartItemsToPricingItems(List<CartItem> items);

    @Mapping(target = "productId", expression = "java(item.getProductId().toString())")
    com.abdelkhalek.storehub.order.pricing.domain.models.Item fromCartItemToPricingItem(CartItem item);

    CartEntity toEntity(Cart cart);

    @Mapping(target = "appliedOfferLabel", source = "appliedOffer", qualifiedByName = "offerLabel")
    CartItemEntity toCartItemEntity(CartItem cartItem);

    @Named("offerLabel")
    default String mapOfferLabel(AppliedOffer offer) {
        return offer != null ? offer.getLabel() : null;
    }

    @Mapping(target = "cartId", source = "id")
    CartResponse fromEntityToResponse(CartEntity entity);

    @Mapping(target = "itemId", source = "id")
    CartItemResponse fromItemEntityToItemResponse(CartItemEntity entity);

    Cart fromEntityToDomain(CartEntity entity);

    @Mapping(target = "cartId", source = "id")
    com.abdelkhalek.storehub.order.pricing.domain.models.Cart toPricingDomain (Cart cart);


    com.abdelkhalek.storehub.order.pricing.domain.models.Item fromGuestItemToPricingDomain (GuestCartItem item);

    List<com.abdelkhalek.storehub.order.pricing.domain.models.Item> fromGuestItemToPricingDomainList (List<GuestCartItem> items);

}
