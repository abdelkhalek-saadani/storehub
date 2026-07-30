package com.abdelkhalek.storehub.order.store.repository;

import com.abdelkhalek.storehub.order.store.model.Store;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface StoreRepository extends ReactiveCrudRepository<Store, UUID> {
    Mono<Boolean> existsBySlug(String slug);
    Mono<Store> findBySlug(String slug);
}
