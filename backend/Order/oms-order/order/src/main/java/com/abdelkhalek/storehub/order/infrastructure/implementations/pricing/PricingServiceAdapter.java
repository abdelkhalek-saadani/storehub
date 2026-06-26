package com.abdelkhalek.storehub.order.infrastructure.implementations.pricing;

import com.abdelkhalek.storehub.order.domain.models.CartItem;
import com.abdelkhalek.storehub.order.domain.models.Order;
import com.abdelkhalek.storehub.order.infrastructure.mappers.CartItemRequestMapper;
import com.abdelkhalek.storehub.order.infrastructure.mappers.PriceRequestMapper;
import com.abdelkhalek.storehub.order.domain.models.*;
import com.abdelkhalek.storehub.order.domain.spi.PricingService;
import com.abdelkhalek.storehub.order.infrastructure.mappers.*;
import com.abdelkhalek.storehub.order.infrastructure.models.pricing.PriceRequest;
import com.abdelkhalek.storehub.order.infrastructure.models.pricing.PriceResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
@Slf4j
public class PricingServiceAdapter implements PricingService {
    @Autowired
    private PricingClient pricingClient;

    @Autowired
    private CartItemRequestMapper cartItemRequestMapper;

    @Autowired
    private PriceRequestMapper priceRequestMapper;

    @Override
    public Mono<Order> calculateOrderTotals(Order order) {

        PriceRequest priceRequest = priceRequestMapper.fromOrder(order);
        Mono<PriceResponse> priceResponseMono = pricingClient.calculateOrderTotals(priceRequest);
        return priceResponseMono
                .map( (priceResponse -> {
                    List<CartItem> items = cartItemRequestMapper.toCartItems(priceResponse.getItems());
                    order.setCartItems(items);
                    log.info("Calculated order total value: {}", priceResponse.getTotal());
                    order.setTotal(priceResponse.getTotal());
                    return order;
                }));
    }
}
