package com.abdelkhalek.storehub.order.cart.repository;


import com.abdelkhalek.storehub.order.cart.entity.CartEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Repository
public interface CartRepository extends ReactiveCrudRepository<CartEntity, UUID> {

    Mono<CartEntity> findByUserIdAndStoreId(UUID userId, UUID storeId);
    Mono<CartEntity> findByGuestIdAndStoreId(UUID guestId, UUID storeId);
}