package com.abdelkhalek.storehub.order.order.mapper;

import com.abdelkhalek.storehub.order.cart.entities.CartItemEntity;
import com.abdelkhalek.storehub.order.order.dto.OrderDto;
import com.abdelkhalek.storehub.order.order.dto.OrderItemDto;
import com.abdelkhalek.storehub.order.order.dto.OrderStatusDto;
import com.abdelkhalek.storehub.order.order.entity.OrderEntity;
import com.abdelkhalek.storehub.order.order.entity.OrderItemEntity;
import com.abdelkhalek.storehub.order.order.models.Order;
import com.abdelkhalek.storehub.order.order.models.OrderItem;
import com.abdelkhalek.storehub.order.order.models.OrderStatus;
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

    OrderStatusDto toDto(OrderStatus status);

    @Mapping(source = "id", target = "itemId")
    @Mapping(target = "appliedOfferLabel", source = "appliedOffer", qualifiedByName = "offerLabel")
    OrderItemDto toOrderItemDto(OrderItem orderItem);


    @Mapping(source = "id", target = "orderId")
    OrderDto toOrderDto(Order order);

    PricesRequest toPricesRequest(Order order);

    List<OrderItem> fromCartItemEntities(List<CartItemEntity> cartItemEntities);

    OrderEntity toEntity(Order order);
    Order fromEntity(OrderEntity orderEntity);

    @Mapping(target = "appliedOfferLabel", source = "appliedOffer", qualifiedByName = "offerLabel")
    OrderItemEntity toOrderItemEntity(OrderItem orderItem);
    @Mapping(source = "appliedOfferLabel", target = "appliedOffer", qualifiedByName = "offer")
    OrderItem fromOrderItemEntity(OrderItemEntity orderItemEntity);

    @Named("offerLabel")
    default String mapOfferLabel(AppliedOffer offer) {
        return offer != null ? offer.getLabel() : null;
    }

    @Named("offer")
    default AppliedOffer mapOffer(String label) {
        var ao = new AppliedOffer();
        ao.setLabel(label);
        return ao;
    }



}