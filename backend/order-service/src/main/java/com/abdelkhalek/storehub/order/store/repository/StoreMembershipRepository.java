package com.abdelkhalek.storehub.order.store.repository;

import com.abdelkhalek.storehub.order.store.model.MembershipRole;
import com.abdelkhalek.storehub.order.store.model.StoreMembership;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;
import java.util.UUID;

public interface StoreMembershipRepository extends ReactiveCrudRepository<StoreMembership, UUID> {
    Mono<Boolean> existsByUserIdAndRole(UUID userId, MembershipRole role);
    Mono<StoreMembership> findByUserIdAndRole(UUID userId, MembershipRole role);

    Mono<Boolean> existsByUserIdAndStoreIdAndRole(UUID userId, UUID storeId, MembershipRole role);
    Mono<Boolean> existsByUserIdAndStoreId(UUID userId, UUID storeId);
    Mono<StoreMembership> findByUserIdAndStoreId(UUID userId, UUID storeId);

    Mono<StoreMembership> findByStoreIdAndRole(UUID storeId, MembershipRole role);
}