package com.abdelkhalek.storehub.order.common.identity;

import com.abdelkhalek.storehub.order.common.config.StorehubProperties;
import com.abdelkhalek.storehub.order.user.model.SignupRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class KeycloakAdminService {

    private final WebClient webClient;
    private final StorehubProperties props;


    public KeycloakAdminService(StorehubProperties props, WebClient keycloakWebClient) {
        this.webClient = keycloakWebClient;
        this.props = props;
    }


    public Mono<Void> addRealmRole(String keycloakUserId, String roleName) {
        return webClient.get()
                .uri("/admin/realms/{realm}/roles/{roleName}", props.realm(), roleName)
                .retrieve()
                .bodyToMono(Map.class)
                .flatMap(roleRepresentation -> webClient.post()
                        .uri("/admin/realms/{realm}/users/{userId}/role-mappings/realm", props.realm(), keycloakUserId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(List.of(roleRepresentation))
                        .retrieve()
                        .toBodilessEntity()
                        .then()
                );
    }

    public Mono<Void> removeRealmRole(String keycloakUserId, String roleName) {
        return webClient.get()
                .uri("/admin/realms/{realm}/roles/{roleName}", props.realm(), roleName)
                .retrieve()
                .bodyToMono(Map.class)
                .flatMap(roleRepresentation -> webClient.method(org.springframework.http.HttpMethod.DELETE)
                        .uri("/admin/realms/{realm}/users/{userId}/role-mappings/realm", props.realm(), keycloakUserId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(List.of(roleRepresentation))
                        .retrieve()
                        .toBodilessEntity()
                        .then()
                )
                .onErrorResume(e -> {
                    System.err.println("Failed to remove role " + roleName + " from user " + keycloakUserId + ": " + e.getMessage());
                    return Mono.empty();
                });
    }

    /**
     * Creates the Keycloak user (identity fields only) and assigns the
     * default 'customer' realm role. Returns the new Keycloak user ID.
     */
    public Mono<String> createUser(SignupRequest req) {
        return webClient.post()
                .uri("/admin/realms/{realm}/users", props.realm())
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(buildUserPayload(req))
                .exchangeToMono(response -> {
                    if (response.statusCode().equals(HttpStatus.CREATED)) {
                        String location = response.headers().header(HttpHeaders.LOCATION).get(0);
                        String userId = location.substring(location.lastIndexOf('/') + 1);
                        return Mono.just(userId);
                    }
                    return response.createException().flatMap(Mono::error);
                })
                .flatMap(userId -> addRealmRole(userId, "CUSTOMER").thenReturn(userId));
    }

    /**
     * Compensating action: deletes the Keycloak user if the local DB save
     * fails after KC user creation succeeded. Best-effort, failure here
     * leaves an orphaned KC user that needs alerting/manual cleanup.
     */
    public Mono<Void> deleteUser(String keycloakUserId) {
        return webClient.delete()
                .uri("/admin/realms/{realm}/users/{userId}", props.realm(), keycloakUserId)
                .retrieve()
                .toBodilessEntity()
                .then()
                .onErrorResume(e -> {

                    log.error("Failed to delete orphaned Keycloak user {}: {}", keycloakUserId, e.getMessage());
                    return Mono.empty();
                });
    }

    private Map<String, Object> buildUserPayload(SignupRequest req) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("username", req.getEmail());
        payload.put("email", req.getEmail());
        payload.put("firstName", req.getFirstName());
        payload.put("lastName", req.getLastName());
        payload.put("enabled", true);
        payload.put("emailVerified", false);
        payload.put("credentials", List.of(Map.of(
                "type", "password",
                "value", req.getPassword(),
                "temporary", false
        )));
        return payload;
    }
}
