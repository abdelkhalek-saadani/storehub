package com.abdelkhalek.storehub.order.cart;

import com.abdelkhalek.storehub.order.cart.domain.CartOwner;
import com.abdelkhalek.storehub.order.cart.service.OwnerResolver;
import com.abdelkhalek.storehub.order.user.entity.User;
import com.abdelkhalek.storehub.order.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.UUID;

import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OwnerResolverTest {

    @Mock private UserRepository userRepository;
    @InjectMocks private OwnerResolver ownerResolver;

    @Test
    void resolveOwner_returnsUserOwner_whenJwtAuthenticationPresent() {
        UUID userId = UUID.randomUUID();
        String keycloakId = "kc-123";
        User user = new User();
        user.setId(userId);

        Jwt jwt = Jwt.withTokenValue("token")
                .header("alg", "none")
                .subject(keycloakId)
                .claim("sub", keycloakId)
                .build();
        JwtAuthenticationToken jwtAuth = new JwtAuthenticationToken(jwt);

        when(userRepository.findByKeycloakId(keycloakId)).thenReturn(Mono.just(user));

        Mono<CartOwner> result = ownerResolver.resolveOwner(null)
                .contextWrite(ReactiveSecurityContextHolder.withAuthentication(jwtAuth));

        StepVerifier.create(result)
                .expectNextMatches(owner -> !owner.isGuest() && owner.userId().equals(userId))
                .verifyComplete();
    }

    @Test
    void resolveOwner_returnsGuestOwner_withProvidedGuestId_whenNoAuthentication() {
        UUID guestId = UUID.randomUUID();

        StepVerifier.create(ownerResolver.resolveOwner(guestId))
                .expectNextMatches(owner -> owner.isGuest() && owner.guestId().equals(guestId))
                .verifyComplete();

        verifyNoInteractions(userRepository);
    }

    @Test
    void resolveOwner_generatesNewGuestId_whenGuestIdNullAndNoAuthentication() {
        StepVerifier.create(ownerResolver.resolveOwner(null))
                .expectNextMatches(owner -> owner.isGuest() && owner.guestId() != null)
                .verifyComplete();
    }

    @Test
    void resolveOwner_fallsBackToGuest_whenJwtValidButUserNotFoundInRepository() {
        String keycloakId = "kc-unknown";
        Jwt jwt = Jwt.withTokenValue("token")
                .header("alg", "none")
                .subject(keycloakId)
                .claim("sub", keycloakId)
                .build();
        JwtAuthenticationToken jwtAuth = new JwtAuthenticationToken(jwt);

        when(userRepository.findByKeycloakId(keycloakId)).thenReturn(Mono.empty());

        Mono<CartOwner> result = ownerResolver.resolveOwner(null)
                .contextWrite(ReactiveSecurityContextHolder.withAuthentication(jwtAuth));

        StepVerifier.create(result)
                .expectNextMatches(CartOwner::isGuest)
                .verifyComplete();
    }
}
