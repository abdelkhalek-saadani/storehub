package com.abdelkhalek.storehub.catalog.store;

import com.abdelkhalek.storehub.catalog.user.UserShadowRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
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

}
