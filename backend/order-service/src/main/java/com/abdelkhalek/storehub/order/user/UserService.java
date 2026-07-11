package com.abdelkhalek.storehub.order.user;

import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Mono<UUID> getCurrentUserId() {
        return ReactiveSecurityContextHolder.getContext()
                .map(SecurityContext::getAuthentication)
                .cast(JwtAuthenticationToken.class)
                .map(auth -> auth.getToken().getSubject())
                .flatMap(userRepository::findIdByKeycloakId)
                .switchIfEmpty(Mono.error(new IllegalStateException("No user found for current keycloak id")));
    }

    public Mono<User> getCurrentUser() {
        return ReactiveSecurityContextHolder.getContext()
                .map(SecurityContext::getAuthentication)
                .cast(JwtAuthenticationToken.class)
                .map(auth -> auth.getToken().getSubject())
                .flatMap(userRepository::findByKeycloakId)
                .switchIfEmpty(Mono.error(new IllegalStateException("No user found for current keycloak id")));

    }

    public Mono<LastStoreResponse> getLastStore() {
        return ReactiveSecurityContextHolder.getContext()
                .map(SecurityContext::getAuthentication)
                .cast(JwtAuthenticationToken.class)
                .map(auth -> auth.getToken().getSubject())
                .flatMap(userRepository::findPreferredStoreIdByKeycloakId)
                .map(LastStoreResponse::new)
                .switchIfEmpty(Mono.error(new IllegalStateException("No user found for current keycloak id")));

    }
}