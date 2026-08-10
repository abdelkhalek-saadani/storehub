package com.abdelkhalek.storehub.catalog.slot.controller;


import com.abdelkhalek.storehub.catalog.slot.entity.SlotConfig;
import com.abdelkhalek.storehub.catalog.slot.service.SlotConfigService;
import com.abdelkhalek.storehub.catalog.store.service.StoreService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;

import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/admin/slot-configs")
@RequiredArgsConstructor
public class SlotConfigController {

    private final SlotConfigService slotConfigService;
    private final StoreService storeService;

    @PostMapping
    public SlotConfig create(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody SlotConfig config
    ) {
        config.setStoreId(storeService.getStoreId(jwt.getSubject()));
        return slotConfigService.create(config);
    }

    // Updates the rule and syncs future, unbooked, non-overridden slots automatically.
    @PutMapping("/{configId}")
    public SlotConfig update(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable UUID configId,
            @Valid @RequestBody SlotConfig config) {
        UUID storeId = storeService.getStoreId(jwt.getSubject());
        return slotConfigService.update(configId, storeId, config);
    }
}
