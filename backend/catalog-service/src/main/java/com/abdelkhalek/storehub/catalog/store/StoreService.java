package com.abdelkhalek.storehub.catalog.store;

import com.abdelkhalek.storehub.catalog.user.UserShadowRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class StoreService {

    private final StoreShadowRepository storeShadowRepository;
    private final UserShadowRepository userShadowRepository;

    public UUID getStoreId(String keycloakId) {
        StoreShadow store =
                storeShadowRepository.findByOwnerId(userShadowRepository.findByKeycloakId(keycloakId)
                        .getId());
        return store.getId();
    }

    public UUID getIdBySlug(String slug) {
        var storeOpt = storeShadowRepository.findBySlug(slug);
        if (storeOpt.isEmpty()) {
            throw new StoreNotFoundException("Store with slug " + slug + " not found");
        }
        return storeOpt.get().getId();

    }

}
