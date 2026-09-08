package com.abdelkhalek.storehub.order.store.controller;

import com.abdelkhalek.storehub.order.store.dto.StoreSummary;
import com.abdelkhalek.storehub.order.store.model.MembershipRole;
import com.abdelkhalek.storehub.order.store.repository.StoreMembershipRepository;
import com.abdelkhalek.storehub.order.store.repository.StoreRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/internal/stores")
@Tag(name = "Store Queries (inter-service)", description = "Inter-service store summary lookups")
public class StoreQueryController {

    private final StoreRepository storeRepository;
    private final StoreMembershipRepository storeMembershipRepository;

    public StoreQueryController(StoreRepository storeRepository, StoreMembershipRepository storeMembershipRepository) {
        this.storeRepository = storeRepository;
        this.storeMembershipRepository = storeMembershipRepository;
    }

    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "List store summaries with their owner",
            description = "Internal endpoint for catalog service, needs service account jwt token")
    @GetMapping
    public Flux<StoreSummary> getStores() {
        return storeRepository.findAll()
                .flatMap(store ->
                        storeMembershipRepository
                                .findByStoreIdAndRole(store.getId(), MembershipRole.STORE_OWNER)
                                .map(membership -> new StoreSummary(
                                        store.getId(),
                                        store.getSlug(),
                                        membership.getUserId(),
                                        "ACTIVE"
                                ))
                );
    }


}
