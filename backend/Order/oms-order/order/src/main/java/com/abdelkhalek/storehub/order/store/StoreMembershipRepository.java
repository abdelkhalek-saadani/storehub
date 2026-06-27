package com.abdelkhalek.storehub.order.store;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;
import java.util.UUID;

public interface StoreMembershipRepository extends ReactiveCrudRepository<StoreMembership, UUID> {
    Mono<Boolean> existsByUserIdAndRole(UUID userId, MembershipRole role);
    Mono<StoreMembership> findByUserIdAndRole(UUID userId, MembershipRole role);

    Mono<Boolean> existsByUserIdAndStoreIdAndRole(UUID userId, UUID storeId, MembershipRole role);
    Mono<Boolean> existsByUserIdAndStoreId(UUID userId, UUID storeId);
    Mono<StoreMembership> findByUserIdAndStoreId(UUID userId, UUID storeId);
}