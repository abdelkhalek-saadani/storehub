package com.abdelkhalek.storehub.order;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Component
public class StoreContextFilter implements WebFilter {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        return ReactiveSecurityContextHolder.getContext()
                .map(SecurityContext::getAuthentication)
                .flatMap(auth -> resolveStoreId(exchange, auth))
                .defaultIfEmpty("")
                .flatMap(storeId -> {
                    exchange.getAttributes().put("storeId", storeId);
                    return chain.filter(exchange);
                });
    }

    private Mono<String> resolveStoreId(ServerWebExchange exchange, Authentication auth) {
        if (auth instanceof JwtAuthenticationToken jwtAuth) {
            String adminStoreId = jwtAuth.getToken().getClaimAsString("storeId");
            if (adminStoreId != null) {
                return Mono.just(adminStoreId);
            }
        }
        // customer: storeId comes from request header (set by Angular after store selection)
        String headerStoreId = exchange.getRequest().getHeaders().getFirst("X-Store-Id");
        return Mono.justOrEmpty(headerStoreId);
    }
}
