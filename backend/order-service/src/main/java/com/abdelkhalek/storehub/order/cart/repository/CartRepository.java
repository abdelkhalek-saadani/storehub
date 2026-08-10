package com.abdelkhalek.storehub.order.cart.repository;


import com.abdelkhalek.storehub.order.cart.entity.CartEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface CartRepository extends ReactiveCrudRepository<CartEntity, UUID> {

    Mono<CartEntity> findByUserIdAndStoreId(UUID userId, UUID storeId);
    Mono<CartEntity> findByGuestIdAndStoreId(UUID guestId, UUID storeId);
}