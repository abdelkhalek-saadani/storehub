package com.abdelkhalek.storehub.order.cart.service;

import com.abdelkhalek.storehub.order.cart.domain.CartOwner;
import com.abdelkhalek.storehub.order.user.entity.User;
import com.abdelkhalek.storehub.order.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OwnerResolver {

    private final UserRepository userRepository;

    public Mono<CartOwner> resolveOwner(UUID guestId) {
        return ReactiveSecurityContextHolder.getContext()
                .map(SecurityContext::getAuthentication)
                .cast(JwtAuthenticationToken.class)
                .map(auth -> auth.getToken().getSubject())
                .flatMap(userRepository::findByKeycloakId)
                .map(User::getId)
                .map(CartOwner::ofUser)
                .switchIfEmpty(Mono.defer(() -> resolveGuest(guestId)));
    }

    private Mono<CartOwner> resolveGuest(UUID guestId) {
        if (guestId == null) guestId = UUID.randomUUID();
        return Mono.just(CartOwner.ofGuest(guestId));
    }
}
