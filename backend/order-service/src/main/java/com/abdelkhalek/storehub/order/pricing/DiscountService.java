package com.abdelkhalek.storehub.order.pricing;


import com.abdelkhalek.storehub.order.pricing.domain.models.Item;
import reactor.core.publisher.Mono;

import java.util.List;

public interface DiscountService {
    Mono<PricingResult> calculateTotal(List<Item> items);
}