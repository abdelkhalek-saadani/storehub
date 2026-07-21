package com.abdelkhalek.storehub.order.order.spi;

import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

public interface ProductService {
    Mono<Boolean> checkAvailability(UUID storeId, UUID cartId);
    Mono<List<UUID>> retain(UUID storeId, UUID cartId);
    Mono<Void> release(List<UUID> retainIds);
}
