package com.abdelkhalek.storehub.order.order.mapper;

import com.abdelkhalek.storehub.order.cart.entities.CartItemEntity;
import com.abdelkhalek.storehub.order.order.entity.OrderEntity;
import com.abdelkhalek.storehub.order.order.entity.OrderItemEntity;
import com.abdelkhalek.storehub.order.order.models.Order;
import com.abdelkhalek.storehub.order.order.models.OrderItem;
import com.abdelkhalek.storehub.order.shared.dto.PriceItemResponse;
import com.abdelkhalek.storehub.order.shared.dto.PricesRequest;
import com.abdelkhalek.storehub.order.shared.model.AppliedOffer;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;

@Mapper(
        componentModel = "spring"
)
public interface OrderMapper {
    List<OrderItem> fromPriceItemsResponse(List<PriceItemResponse> priceItemsResponse);



    PricesRequest toPricesRequest(Order order);

    List<OrderItem> fromCartItemEntities(List<CartItemEntity> cartItemEntities);

    OrderEntity toEntity(Order order);
    Order fromEntity(OrderEntity orderEntity);

    @Mapping(target = "appliedOfferLabel", source = "appliedOffer", qualifiedByName = "offerLabel")
    OrderItemEntity toOrderItemEntity(OrderItem orderItem);

    @Named("offerLabel")
    default String mapOfferLabel(AppliedOffer offer) {
        return offer != null ? offer.getLabel() : null;
    }

}