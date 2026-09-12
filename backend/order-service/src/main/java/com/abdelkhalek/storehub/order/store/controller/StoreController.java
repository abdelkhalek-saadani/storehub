package com.abdelkhalek.storehub.order.store.controller;

import com.abdelkhalek.storehub.order.user.service.KeycloakAdminService;
import com.abdelkhalek.storehub.order.store.dto.CreateStoreRequest;
import com.abdelkhalek.storehub.order.store.dto.StoreDto;
import com.abdelkhalek.storehub.order.store.entity.Store;
import com.abdelkhalek.storehub.order.store.model.MembershipRole;
import com.abdelkhalek.storehub.order.store.repository.StoreMembershipRepository;
import com.abdelkhalek.storehub.order.store.repository.StoreRepository;
import com.abdelkhalek.storehub.order.store.service.StoreService;
import com.abdelkhalek.storehub.order.user.repository.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@RequestMapping("/api/stores")
@RequiredArgsConstructor
@Tag(name = "Stores", description = "Store creation and lookup")
public class StoreController {

    private final StoreRepository storeRepository;
    private final StoreMembershipRepository membershipRepository;
    private final UserRepository userRepository;
    private final KeycloakAdminService keycloakAdminService;
    private final StoreService storeService;


    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Create a new store for the authenticated user")
    @ApiResponse(responseCode = "404", description = "User not found")
    @ApiResponse(responseCode = "409", description = "User already owns a store")
    @PostMapping
    public Mono<ResponseEntity<Store>> createStore(@Valid @RequestBody CreateStoreRequest req,
                                                   @AuthenticationPrincipal Jwt jwt) {
        String keycloakId = jwt.getSubject(); // 'sub' claim, the Keycloak user ID

        return userRepository.findByKeycloakId(keycloakId)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found")))
                .flatMap(user -> membershipRepository.existsByUserIdAndRole(user.getId(), MembershipRole.STORE_OWNER)
                        .flatMap(alreadyOwner -> {
                            if (alreadyOwner) {
                                return Mono.error(new ResponseStatusException(
                                        HttpStatus.CONFLICT, "User already owns a store"));
                            }
                            return storeService.createStoreForUser(req, user, keycloakId);
                        })
                );
    }

    @Operation(summary = "List all stores")
    @GetMapping()
    public Flux<StoreDto> getStores() {
        return storeRepository.findAll()
                .doOnNext((store -> log.debug("find all store result {}", store)))
                .map(StoreDto::from)
                .switchIfEmpty(Flux.empty());
    }

    @Operation(summary = "Get a store by its slug")
    @ApiResponse(responseCode = "200", description = "Store found")
    @ApiResponse(responseCode = "404", description = "Store not found")
    @GetMapping("by-slug/{slug}")
    public Mono<ResponseEntity<StoreDto>> findBySlug(@PathVariable String slug) {
        return storeRepository.findBySlug(slug)
                .doOnNext(s -> log.debug("found store {}", s))
                .map(s -> ResponseEntity.ok(new StoreDto(s.getId(), s.getSlug(), s.getName())))
                .switchIfEmpty(Mono.just(ResponseEntity.notFound().build()));
    }


}