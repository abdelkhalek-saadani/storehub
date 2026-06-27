package com.abdelkhalek.storehub.order.store;

import com.abdelkhalek.storehub.order.auth.KeycloakAdminService;
import com.abdelkhalek.storehub.order.auth.User;
import com.abdelkhalek.storehub.order.auth.UserRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/stores")
public class StoreController {

    private final StoreRepository storeRepository;
    private final StoreMembershipRepository membershipRepository;
    private final UserRepository userRepository;
    private final KeycloakAdminService keycloakAdminService;

    public StoreController(StoreRepository storeRepository,
                           StoreMembershipRepository membershipRepository,
                           UserRepository userRepository,
                           KeycloakAdminService keycloakAdminService) {
        this.storeRepository = storeRepository;
        this.membershipRepository = membershipRepository;
        this.userRepository = userRepository;
        this.keycloakAdminService = keycloakAdminService;
    }

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
                            return createStoreForUser(req, user, keycloakId);
                        })
                );
    }

    private Mono<ResponseEntity<Store>> createStoreForUser(CreateStoreRequest req, User user, String keycloakId) {
        Store store = new Store();
        store.setName(req.getName());
        store.setDescription(req.getDescription());

        return storeRepository.save(store)
                .flatMap(savedStore ->
                        membershipRepository.save(new StoreMembership(user.getId(), savedStore.getId(), MembershipRole.STORE_OWNER))
                                .then(keycloakAdminService.addRealmRole(keycloakId, MembershipRole.STORE_OWNER.name()))
                                .thenReturn(ResponseEntity.status(HttpStatus.CREATED).body(savedStore))
                                .onErrorResume(err -> rollback(savedStore,user,  keycloakId, err))
                );
    }

    private Mono<ResponseEntity<Store>> rollback(Store savedStore, User user, String keycloakId, Throwable err) {
        // Best-effort cleanup: remove DB rows + revoke role if anything failed mid-chain.
        return membershipRepository.deleteAll(
                        membershipRepository.findByUserIdAndRole(user.getId(), MembershipRole.STORE_OWNER))
                .then(storeRepository.delete(savedStore))
                .then(keycloakAdminService.removeRealmRole(keycloakId, MembershipRole.STORE_OWNER.name()))
                .then(Mono.error(new ResponseStatusException(
                        HttpStatus.INTERNAL_SERVER_ERROR, "Store creation failed, please retry")));
    }
}