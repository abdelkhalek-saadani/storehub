package com.abdelkhalek.storehub.order.order.spi;

import com.abdelkhalek.storehub.order.order.models.Order;
import reactor.core.publisher.Mono;

public interface PricingService {

    Mono<Order> calculateOrderTotals(Order order);

}
