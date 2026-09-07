package com.abdelkhalek.storehub.catalog.slot.controller;


import com.abdelkhalek.storehub.catalog.slot.entity.SlotConfig;
import com.abdelkhalek.storehub.catalog.slot.service.SlotConfigService;
import com.abdelkhalek.storehub.catalog.store.service.StoreService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;

import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/admin/slot-configs")
@RequiredArgsConstructor
@Tag(name = "Slot Configs", description = "Admin management of recurring slot generation rules")
public class SlotConfigController {

    private final SlotConfigService slotConfigService;
    private final StoreService storeService;

    @Operation(summary = "Create a new slot config for store owned by the authenticated user")
    @ApiResponse(responseCode = "200", description = "Config created")
    @ApiResponse(responseCode = "400", description = "Invalid request body")
    @PostMapping
    public SlotConfig create(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody SlotConfig config
    ) {
        config.setStoreId(storeService.getStoreId(jwt.getSubject()));
        return slotConfigService.create(config);
    }

    /*
     * Updates the rule and syncs future, unbooked, non-overridden slots automatically.
     */
    @Operation(summary = "Update a slot config",
            description = "Automatically syncs future, unbooked, non-overridden slots to match the updated rule.")
    @ApiResponse(responseCode = "200", description = "Config updated")
    @ApiResponse(responseCode = "404",
            description = "Config not found for store owned by this user")
    @PutMapping("/{configId}")
    public SlotConfig update(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable UUID configId,
            @Valid @RequestBody SlotConfig config) {
        UUID storeId = storeService.getStoreId(jwt.getSubject());
        return slotConfigService.update(configId, storeId, config);
    }
}
