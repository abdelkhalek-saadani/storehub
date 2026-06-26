package com.abdelkhalek.storehub.order.domain.spi;

import com.abdelkhalek.storehub.order.domain.models.Order;
import reactor.core.publisher.Mono;

public interface PricingService {

    Mono<Order> calculateOrderTotals(Order order);

}
