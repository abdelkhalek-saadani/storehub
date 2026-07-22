package com.abdelkhalek.storehub.order.order.mapper;

import com.abdelkhalek.storehub.order.order.entity.OrderEntity;
import com.abdelkhalek.storehub.order.shared.dto.PriceItemResponse;
import com.abdelkhalek.storehub.order.shared.dto.PricesRequest;
import com.abdelkhalek.storehub.order.order.models.Order;
import com.abdelkhalek.storehub.order.order.models.OrderItem;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(
        componentModel = "spring"
)
public interface OrderMapper {
    List<OrderItem> fromPriceItemsResponse(List<PriceItemResponse> priceItemsResponse);


    PricesRequest toPricesRequest(Order order);

    OrderEntity toEntity(Order order);
    Order fromEntity(OrderEntity orderEntity);
}