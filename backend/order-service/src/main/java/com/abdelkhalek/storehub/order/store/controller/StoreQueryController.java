package com.abdelkhalek.storehub.order.store.controller;

import com.abdelkhalek.storehub.order.store.model.MembershipRole;
import com.abdelkhalek.storehub.order.store.model.StoreSummary;
import com.abdelkhalek.storehub.order.store.repository.StoreMembershipRepository;
import com.abdelkhalek.storehub.order.store.repository.StoreRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/api/stores")
public class StoreQueryController {

    private final StoreRepository storeRepository;
    private final StoreMembershipRepository storeMembershipRepository;

    public StoreQueryController(StoreRepository storeRepository, StoreMembershipRepository storeMembershipRepository) {
        this.storeRepository = storeRepository;
        this.storeMembershipRepository = storeMembershipRepository;
    }

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
