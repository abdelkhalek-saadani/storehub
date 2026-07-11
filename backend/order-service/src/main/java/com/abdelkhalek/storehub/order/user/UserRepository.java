package com.abdelkhalek.storehub.order.user;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Repository
public interface UserRepository extends ReactiveCrudRepository<User, UUID> {
    Mono<Boolean> existsByEmail(String email);
    Mono<User> findByKeycloakId(String keycloakId);
    Mono<UUID> findIdByKeycloakId(String keycloakId);

    Mono<User> findByEmail(String email);

    @Query("SELECT preferred_store_id FROM users WHERE keycloak_id = :keycloakId")
    Mono<UUID> findPreferredStoreIdByKeycloakId(String keycloakId);
}
