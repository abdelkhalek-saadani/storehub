package com.abdelkhalek.storehub.order.common.security;

import com.abdelkhalek.storehub.order.store.model.MembershipRole;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverterAdapter;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import reactor.core.publisher.Mono;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Configuration
@EnableWebFluxSecurity
@Slf4j
public class SecurityConfig {

    @Value("${spring.application.cors.allowed-origins}")
    private List<String> allowedOrigins;

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http,
                                                         StoreContextFilter storeContextFilter) {
        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable) // stateless JWT API, no cookies
                .authorizeExchange(exchange -> exchange
                        .pathMatchers("/api/stores/by-slug/**").permitAll()
                        .pathMatchers("/api/orders/*/track").permitAll()
                        .pathMatchers("/api/auth/**").permitAll()
                        .pathMatchers("/api/cart/quote/**").permitAll()
                        .pathMatchers("/internal/**").hasRole("SERVICE") // service-to-service
                        // Guest checkout
                        .pathMatchers(HttpMethod.POST,"/api/orders").permitAll()
                        .pathMatchers(HttpMethod.POST,"/api/orders/guest").permitAll()
                        .pathMatchers("/api/orders/**").hasRole("CUSTOMER")
                        .pathMatchers("/api/cart/**").permitAll()
                        .pathMatchers("/api/stores/*/employees/**").hasRole(MembershipRole.STORE_OWNER.name())
                        .anyExchange().authenticated()
                )
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter()))
                )
                .addFilterAfter(storeContextFilter, SecurityWebFiltersOrder.AUTHENTICATION)
                .build();
    }

    // Keycloak puts roles under realm_access.roles by default, Spring doesn't unpack this
    // automatically, so we extract it ourselves into GrantedAuthorities.
    private Converter<Jwt, Mono<AbstractAuthenticationToken>> jwtAuthenticationConverter() {
        JwtAuthenticationConverter delegate = new JwtAuthenticationConverter();
        delegate.setJwtGrantedAuthoritiesConverter(this::extractRealmRoles);
        return new ReactiveJwtAuthenticationConverterAdapter(delegate);
    }

    private Collection<GrantedAuthority> extractRealmRoles(Jwt jwt) {
        Map<String, Object> realmAccess = jwt.getClaim("realm_access");
        if (realmAccess == null) return List.of();

        Object rolesObj = realmAccess.get("roles");
        if (!(rolesObj instanceof List<?> rawRoles)) {
            log.warn("realm_access.roles claim is not a list (subject={}): {}", jwt.getSubject(), rolesObj);
            return List.of();
        }

        return rawRoles.stream()
                .filter(r -> {
                    boolean isString = r instanceof String;
                    if (!isString) {
                        log.warn("Dropping non-string role claim (subject={}): {}", jwt.getSubject(), r);
                    }
                    return isString;
                })
                .map(r -> new SimpleGrantedAuthority("ROLE_" + ((String) r).toUpperCase()))
                .collect(Collectors.toList());
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(allowedOrigins);
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("Authorization", "Content-Type", "Idempotency-Key", "X-Guest-Id"));
        config.setExposedHeaders(List.of("X-Guest-Id"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/**", config);
        return source;
    }
}