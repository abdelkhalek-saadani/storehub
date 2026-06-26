package com.abdelkhalek.storehub.order.domain.spi;

import com.abdelkhalek.storehub.order.domain.models.CartItem;
import com.abdelkhalek.storehub.order.domain.models.Store;
import com.abdelkhalek.storehub.order.domain.models.*;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

public interface ProductService {
    Mono<Boolean> checkAvailability(List<CartItem> items, Store store);
    Mono<UUID> retain(List<CartItem> items, Store store);
    Mono<Void> release(UUID retainId);
}
