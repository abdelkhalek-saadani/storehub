package com.abdelkhalek.storehub.order.store.service;

import com.abdelkhalek.storehub.order.common.identity.KeycloakAdminService;
import com.abdelkhalek.storehub.order.store.StoreEventPublisher;
import com.abdelkhalek.storehub.order.store.model.CreateStoreRequest;
import com.abdelkhalek.storehub.order.store.model.MembershipRole;
import com.abdelkhalek.storehub.order.store.model.Store;
import com.abdelkhalek.storehub.order.store.model.StoreMembership;
import com.abdelkhalek.storehub.order.store.repository.StoreMembershipRepository;
import com.abdelkhalek.storehub.order.store.repository.StoreRepository;
import com.abdelkhalek.storehub.order.user.model.User;
import com.github.slugify.Slugify;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class StoreService {

    private final StoreRepository storeRepository;
    private final StoreMembershipRepository membershipRepository;
    private final KeycloakAdminService keycloakAdminService;
    private final StoreEventPublisher storeEventPublisher;


    public Mono<ResponseEntity<Store>> createStoreForUser(CreateStoreRequest req, User user,
                                                     String keycloakId) {
        Store s = new Store();
        s.setName(req.getName());
        s.setDescription(req.getDescription());
        s.setAddress(req.getAddress());

        return generateUniqueSlug(s.getName()).map(s::withSlug)
                .flatMap(storeRepository::save)
                .flatMap(savedStore ->
                        membershipRepository.save(new StoreMembership(user.getId(), savedStore.getId(), MembershipRole.STORE_OWNER))
                                .then(keycloakAdminService.addRealmRole(keycloakId, MembershipRole.STORE_OWNER.name()))
                                .then(storeEventPublisher.storeCreated(user,savedStore))
                                .thenReturn(ResponseEntity.status(HttpStatus.CREATED)
                                        .body(savedStore))
                                .onErrorResume(err -> rollback(savedStore, user, keycloakId, err))
                );
    }

    private Mono<String> generateUniqueSlug(String name) {
        Slugify slugify = Slugify.builder().build();
        String base = slugify.slugify(name);

        return checkSlug(base, base, 1);
    }

    private Mono<String> checkSlug(String base, String candidate, int suffix) {
        return storeRepository.existsBySlug(candidate)
                .flatMap(exists -> {
                    if (!exists) {
                        return Mono.just(candidate);
                    }
                    String next = base + "-" + (suffix + 1);
                    return checkSlug(base, next, suffix + 1);
                });
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
