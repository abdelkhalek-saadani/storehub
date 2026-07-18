package com.abdelkhalek.storehub.order.store.employee;


import com.abdelkhalek.storehub.order.store.model.MembershipRole;
import com.abdelkhalek.storehub.order.store.repository.StoreMembershipRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
public class StoreAuthorizationService {

    private final StoreMembershipRepository membershipRepository;

    public StoreAuthorizationService(StoreMembershipRepository membershipRepository) {
        this.membershipRepository = membershipRepository;
    }

    /**
     * Verifies the given local user is the OWNER of the given store.
     * Returns Mono.empty() on success (void-style gate), errors with 403 otherwise.
     */
    public Mono<Void> requireOwnerOf(UUID userId, UUID storeId) {
        return membershipRepository.existsByUserIdAndStoreIdAndRole(userId, storeId, MembershipRole.STORE_OWNER)
                .flatMap(isOwner -> {
                    if (!isOwner) {
                        return Mono.error(new ResponseStatusException(
                                HttpStatus.FORBIDDEN, "You do not own this store"));
                    }
                    return Mono.empty();
                });
    }
}
